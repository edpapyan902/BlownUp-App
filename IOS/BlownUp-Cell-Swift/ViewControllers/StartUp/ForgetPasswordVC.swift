//
//  ForgetPasswordVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 17/05/2021.
//

import Foundation
import UIKit

class ForgetPasswordVC: BaseVC {
    
    @IBOutlet weak var imgBack: UIImageView!
    @IBOutlet weak var txtEmail: TextInput!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initLayout()
    }
    
    func initLayout() {
        self.imgBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onBack)))
    }
    
    @objc func onBack() {
        self.gotoPageVC(VC_LOGIN)
    }
    
    @IBAction func sendMail(_ sender: Any) {
        let email = txtEmail.getText()
        if !email.isValidEmail() {
            self.showWarning("Please enter vaild email address")
            return
        }
        
        let params: [String: Any] = [
            "email": email
        ]
        
        self.showLoading(self)
        
        API.instance.forgetPassword(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let forgetPasswordRes: ForgetPasswordRes = response.result.value!
                if forgetPasswordRes.success {
                    self.showSuccess(forgetPasswordRes.message)
                    
                    print("verify code", forgetPasswordRes.data.verify_code)
                    
                    let alert = UIAlertController(title: "Check Verification Code", message: "Be sure to check your Spam Folder for your verification code. It might be going to there.", preferredStyle: .alert)
                    alert.addAction(UIAlertAction(title: NSLocalizedString("OK", comment: "Default action"), style: .default, handler: { _ in
                        self.gotoVerifyCodeVC(email: email, verify_code: forgetPasswordRes.data.verify_code)
                    }))
                    self.present(alert, animated: true, completion: nil)
                    
                } else {
                    self.showError(forgetPasswordRes.message)
                }
            }
        }
    }
}
