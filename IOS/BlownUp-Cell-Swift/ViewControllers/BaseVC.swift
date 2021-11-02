//
//  BaseVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 29/04/2021.
//

import Foundation
import UIKit
import KRProgressHUD
import SwiftMessages

class BaseVC : UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Rotate Restict
        (UIApplication.shared.delegate as! AppDelegate).restrictRotation = .portrait
        setStatusBarStyle(false)
    }
    
    func setStatusBarStyle(_ isLightMode: Bool) {
        if isLightMode {
            UIApplication.shared.statusBarStyle = .lightContent
        } else {
            UIApplication.shared.statusBarStyle = .darkContent
        }
    }
    
    func gotoPageVC(_ name: String) {
        let storyboad = UIStoryboard(name: name, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: name)
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.window?.rootViewController = targetVC
        UIApplication.shared.keyWindow?.rootViewController = targetVC
    }
    
    func gotoModalVC(_ name: String, _ isFullScreen: Bool) {
        let storyboad = UIStoryboard(name: name, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: name)
        if isFullScreen {
            targetVC.modalPresentationStyle = .fullScreen
        }
        self.present(targetVC, animated: true, completion: nil)
    }
    
    func gotoMainVC(_ type: Int) {
        let storyboad = UIStoryboard(name: VC_MAIN_TAB, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: VC_MAIN_TAB) as! MainTabVC
        targetVC.type = type
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.window?.rootViewController = targetVC
        UIApplication.shared.keyWindow?.rootViewController = targetVC
    }
    
    func gotoScheduleAddVC(_ schedule: Schedule?) {
        let storyboad = UIStoryboard(name: VC_SCHEDULE_ADD, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: VC_SCHEDULE_ADD) as! ScheduleAddVC
        targetVC.currentSchedule = schedule
        targetVC.modalPresentationStyle = .fullScreen
        self.present(targetVC, animated: true, completion: nil)
    }
    
    func gotoContactAddVC(_ contact: Contact?) {
        let storyboad = UIStoryboard(name: VC_CONTACT_ADD, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: VC_CONTACT_ADD) as! ContactAddVC
        targetVC.currentContact = contact
        targetVC.modalPresentationStyle = .fullScreen
        self.present(targetVC, animated: true, completion: nil)
    }
    
    func gotoVerifyCodeVC(email: String, verify_code: Int) {
        let storyboad = UIStoryboard(name: VC_VERIFY_CODE, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: VC_VERIFY_CODE) as! VerifyCodeVC
        targetVC.email = email
        targetVC.m_VerifyCode = verify_code
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.window?.rootViewController = targetVC
        UIApplication.shared.keyWindow?.rootViewController = targetVC
    }
    
    func gotoResetPasswordVC(email: String) {
        let storyboad = UIStoryboard(name: VC_RESET_PASSWORD, bundle: nil)
        let targetVC = storyboad.instantiateViewController(withIdentifier: VC_RESET_PASSWORD) as! ResetPasswordVC
        targetVC.email = email
        
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.window?.rootViewController = targetVC
        UIApplication.shared.keyWindow?.rootViewController = targetVC
    }
    
    func showLoading(_ viewController: UIViewController) {
        let primaryColor = UIColor.init(named: "colorPrimary")
        let styles : KRProgressHUDStyle = .custom (background: .white,text : primaryColor!, icon: primaryColor! )
        KRProgressHUD.set(style: styles)
        KRProgressHUD.set(activityIndicatorViewColors: [primaryColor!, primaryColor!])
        KRProgressHUD.showOn(viewController).show()
    }
    
    func hideLoading() {
        KRProgressHUD.dismiss()
    }
    
    func showMessage(_ body: String, _ type: Int) {
        if body.isEmpty() {
            return
        }
        
        let view = MessageView.viewFromNib(layout: .cardView)
        var title: String
        if type == 0 {
            view.configureTheme(.success)
            title = "Success"
        }
        else if type == 1 {
            view.configureTheme(.warning)
            title = "Warning"
        }
        else {
            view.configureTheme(.error)
            title = "Error"
        }
        
        (view.backgroundView as? CornerRoundingView)?.cornerRadius = 10
        view.configureDropShadow()
        view.configureContent(title: title, body: body)
        view.button?.isHidden = true
        
        var config = SwiftMessages.Config()
        config.presentationStyle = .bottom
        
        SwiftMessages.show(config: config, view: view)
    }
    
    func showSuccess(_ message: String) {
        showMessage(message, 0)
    }
    
    func showWarning(_ message: String) {
        showMessage(message, 1)
    }
    
    func showError(_ message: String) {
        showMessage(message, 2)
    }
}
