//
//  VerifyCodeVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 17/05/2021.
//

import Foundation
import KWVerificationCodeView

class VerifyCodeVC: BaseVC {
    
    var email = ""
    var m_VerifyCode = -1
    
    var verify_Timer: Timer? = nil
    
    @IBOutlet weak var lblResend: UILabel!
    @IBOutlet weak var verifyCodeView: KWVerificationCodeView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initLayout()
    }
    
    func initLayout() {
        self.lblResend.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.resendCode)))
    }
    
    @IBAction func verifyCode(_ sender: Any) {
        let verifyCode = verifyCodeView.getVerificationCode()
        if verifyCode.count == 6 {
            self.showLoading(self)
            self.verify_Timer = Timer.scheduledTimer(timeInterval: 2, target: self, selector: #selector(self.confirmCode), userInfo: nil, repeats: false)
        }
    }
    
    @objc func confirmCode() {
        self.hideLoading()
        
        self.verify_Timer?.invalidate()
        
        let verifyCode = verifyCodeView.getVerificationCode()
        if Int(verifyCode) == m_VerifyCode {
            self.gotoResetPasswordVC(email: email)
        } else {
            self.showWarning("Please enter correct code")
        }
    }
    
    @objc func resendCode() {
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
                    
                    self.m_VerifyCode = forgetPasswordRes.data.verify_code
                    print("verify code", self.m_VerifyCode)
                } else {
                    self.showError(forgetPasswordRes.message)
                }
            }
        }
    }
}
