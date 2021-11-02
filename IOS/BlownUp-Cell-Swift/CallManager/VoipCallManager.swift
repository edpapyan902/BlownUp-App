//
//  VoipCallManager.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 19/05/2021.
//

import Foundation
import UIKit
import CallKit
import PushKit

class VoipCallManager: NSObject, UNUserNotificationCenterDelegate {
    
    static let shared = VoipCallManager()
    private var provider: CXProvider?
    private let voipRegistry = PKPushRegistry(queue: nil)
    private var endCall_timer: Timer? = nil
    private var incoming_uuid = UUID()
    
    private override init() {
        super.init()
    }
    
    public func configureCall() {
        self.registerForRemoteNotification()
        self.configureSystemCallProvider()
        self.configurePushKit()
    }
    
    private func registerForRemoteNotification() {
        if #available(iOS 10.0, *) {
            UNUserNotificationCenter.current().delegate = self
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            UIApplication.shared.registerUserNotificationSettings(settings)
        }
        UIApplication.shared.registerForRemoteNotifications()
    }
    
    private func configureSystemCallProvider() {
        let config = CXProviderConfiguration()
        config.supportsVideo = false
        config.supportedHandleTypes = [.generic, .phoneNumber]
        config.iconTemplateImageData = UIImage(named: "logo_green")!.pngData()
        config.ringtoneSound = "Ringtone.mp3"
        
        provider = CXProvider(configuration: config)
        provider?.setDelegate(self, queue: DispatchQueue.main)
    }
    
    private func configurePushKit() {
        voipRegistry.delegate = self
        voipRegistry.desiredPushTypes = [.voIP]
    }
    
    public func handleIncomingCall(name: String, phoneNumber: String, avatar: String) {
        if self.endCall_timer != nil {
            self.endCall_timer?.invalidate()
        }
        
        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .phoneNumber, value: phoneNumber)
        
        self.incoming_uuid = UUID()
        self.provider?.reportNewIncomingCall(with: self.incoming_uuid, update: update) { error in }
        
        self.endCall_timer = Timer.scheduledTimer(timeInterval: 20, target: self, selector: #selector(self.endCall), userInfo: nil, repeats: false)
    }
    
    @objc private func endCall() {
        self.endCall_timer?.invalidate()
        self.provider?.reportCall(with: self.incoming_uuid, endedAt: Date(), reason: .unanswered)
    }
}

extension VoipCallManager: CXProviderDelegate {
    func providerDidReset(_ provider: CXProvider) {
    }
    
    func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        if self.endCall_timer != nil {
            self.endCall_timer?.invalidate()
        }
        action.fulfill()
    }
    
    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        if self.endCall_timer != nil {
            self.endCall_timer?.invalidate()
        }
        action.fulfill()
    }
    
    func provider(_ provider: CXProvider, perform action: CXStartCallAction) {
        action.fulfill()
    }
}

extension VoipCallManager: PKPushRegistryDelegate {
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let parts = pushCredentials.token.map { String(format: "%02.2hhx", $0) }
        let token = parts.joined()
        
        Store.instance.voipToken = token
    }
    
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        if type == .voIP {
            if let callData = payload.dictionaryPayload as? [String : Any], let name = callData["name"] as? String, let phoneNumber = callData["phoneNumber"] as? String, let avatar = callData["avatar"] as? String {
                
                self.handleIncomingCall(name: name, phoneNumber: phoneNumber, avatar: avatar)
                
                completion()
            }
        }
    }
}
