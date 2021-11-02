//
//  ResetPasswordVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 17/05/2021.
//

import Foundation

class ResetPasswordVC: BaseVC {
    
    var email = ""
    
    @IBOutlet weak var txtConPwd: TextInput!
    @IBOutlet weak var txtPwd: TextInput!
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func resetPassword(_ sender: Any) {
        let password = txtPwd.getText()
        let conPassword = txtConPwd.getText()
        
        if password.count < 6 {
            self.showWarning("Password should be at least 6 characters")
            return
        }
        if conPassword != password {
            self.showWarning("Please enter correct confirm password")
            return
        }
        
        let params: [String: Any] = [
            "email": email,
            "password": password
        ]
        
        self.showLoading(self)
        
        API.instance.resetPassword(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let resetPasswordRes: ResetPasswordRes = response.result.value!
                
                if resetPasswordRes.success! {
                    self.showSuccess(resetPasswordRes.message!)
                    
                    let data = resetPasswordRes.data
                    
                    Store.instance.apiToken = (data?.user?.token)!
                    Store.instance.user = (data?.user)!
                    Store.instance.rememberMe = false
                    
                    Store.instance.charged = (data?.charged)!
                    
                    if !Store.instance.charged {
                        self.gotoPageVC(VC_CARD_REGISTER)
                    }
                    else {
                        self.gotoPageVC(VC_RECENT_CALL)
                    }
                } else {
                    self.showError(resetPasswordRes.message!)
                }
            }
        }
    }
}
