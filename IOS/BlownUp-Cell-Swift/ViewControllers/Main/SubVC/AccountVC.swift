//
//  AccountVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//


import Foundation
import UIKit

class AccountVC: BaseVC {
    
    @IBOutlet weak var imgScheduleAdd: UIImageView!
    @IBOutlet weak var txtPhone: TextInput!
    @IBOutlet weak var txtPassword: TextInput!
    @IBOutlet weak var lblEmail: UILabel!
    
    var m_Password = ""
    var m_SpoofPhoneNumber = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        initLayout()
        initData()
    }
    
    func initLayout() {
        self.imgScheduleAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onScheduleAddClicked)))
    }
    
    @objc func onScheduleAddClicked() {
        self.gotoScheduleAddVC(nil)
    }
    
    func initData() {
        self.lblEmail.text = Store.instance.user?.email
        self.txtPassword.setEnabled(true)
        
        if (Store.instance.user?.is_social)! > 0 {
            self.txtPassword.setEnabled(false)
        }
        
        self.txtPhone.setText((Store.instance.user?.spoof_phone_number)!)
    }
    
    @IBAction func onBtnLogoutClicked(_ sender: Any) {
        Store.instance.rememberMe = false
        Store.instance.apiToken = ""
        Store.instance.charged = false
        Store.instance.user = nil
        
        self.gotoPageVC(VC_LOGIN)
    }
    
    @IBAction func onBtnUpdateClicked(_ sender: Any) {
        var isSavePwd = false, isSavePhone = false
        
        let password = self.txtPassword.getText()
        if !password.isEmpty() {
            if password.count < 6 {
                self.showWarning("Password should be over 6 characters.")
            } else {
                m_Password = password
                isSavePwd = true
            }
        }
        
        let phone_number = self.txtPhone.getText()
        if phone_number.isValidePhone() && Store.instance.user?.spoof_phone_number != phone_number {
            m_SpoofPhoneNumber = phone_number.formatPhoneNumber()
            isSavePhone = true
        }
        
        if !isSavePwd && !isSavePhone {
            self.showWarning("There is nothing to update.")
            return
        }
        
        var params: [String: Any] = [
            "": "",
        ]
        
        if !m_Password.isEmpty() {
            params["password"] = m_Password
        }
        if !m_SpoofPhoneNumber.isEmpty() {
            params["spoof_phone_number"] = m_SpoofPhoneNumber
        }
        
        self.txtPhone.clearFocus()
        self.txtPassword.clearFocus()
        
        self.showLoading(self)
        
        API.instance.updateAccount(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let accountUpdateRes: AccountUpdateRes = response.result.value!
                
                if accountUpdateRes.password_success {
                    var user = accountUpdateRes.data.user
                    user?.token = accountUpdateRes.data.user_access_token
                    
                    Store.instance.apiToken = accountUpdateRes.data.user_access_token!
                    
                    Store.instance.user = user
                    Store.instance.rememberMe = false
                    
                    self.txtPassword.setText("")
                    self.m_Password = ""
                }
                if accountUpdateRes.spoof_phone_success && !accountUpdateRes.password_success {
                    var user = accountUpdateRes.data.user
                    user?.token = Store.instance.apiToken
                    
                    Store.instance.user = user
                    
                    self.m_SpoofPhoneNumber = ""
                }
                
                self.showSuccess(accountUpdateRes.message)
            }
        }
    }
}
