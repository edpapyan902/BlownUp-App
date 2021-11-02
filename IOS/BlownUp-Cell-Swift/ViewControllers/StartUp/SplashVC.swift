//
//  SplashVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import UIKit

class SplashVC: BaseVC {
    
    var loadTimer: Timer? = nil
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        getChargeStatus()
    }
    
    func getChargeStatus() {
        if Store.instance.apiToken.isEmpty() {
            self.loadTimer = Timer.scheduledTimer(timeInterval: 0.0, target: self, selector: #selector(self.goNext), userInfo: nil, repeats: true)
        }
        else {
            API.instance.getChargeStatus() { (response) in
                if response.error == nil {
                    let subscriptionRes: ChargeStatusRes = response.result.value!
                    
                    if subscriptionRes.success! {
                        let data = subscriptionRes.data
                        
                        Store.instance.charged = (data?.charged)!
                    }
                }
                
                self.goNext()
            }
        }
    }
    
    @objc func goNext() {
        if loadTimer != nil {
            loadTimer?.invalidate()
        }
        
        let rememberMe = Store.instance.rememberMe
        let apiToken = Store.instance.apiToken
        let charged = Store.instance.charged
        
        if !apiToken.isEmpty() && rememberMe {
            if !charged {
                self.gotoPageVC(VC_CARD_REGISTER)
            }
            else {
                self.gotoPageVC(VC_RECENT_CALL)
            }
        }
        else {
            self.gotoPageVC(VC_LOGIN)
        }
    }
}
