//
//  SignUpVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import UIKit
import AuthenticationServices

class SignUpVC: BaseVC {
    
    @IBOutlet weak var appleAuthProviderView: UIStackView!
    @IBOutlet weak var btnLogin: UIButton!
    @IBOutlet weak var swtTerm: UISwitch!
    @IBOutlet weak var txtSpoofPhone: TextInput!
    @IBOutlet weak var txtConPwd: TextInput!
    @IBOutlet weak var txtPwd: TextInput!
    @IBOutlet weak var txtEmail: TextInput!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        initLayout()
    }
    
    func initLayout() {
        //Apple Sign In Button Set
        let authorizationButton = ASAuthorizationAppleIDButton()
        authorizationButton.addTarget(self, action: #selector(handleAppleAuth), for: .touchUpInside)
        self.appleAuthProviderView.addArrangedSubview(authorizationButton)
    }
    
    @IBAction func goLogin(_ sender: Any) {
        self.gotoPageVC(VC_LOGIN)
    }
    
    @IBAction func goTermsLink(_ sender: Any) {
        if let url = URL(string: TERMS_CONDITIONS_URL), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.openURL(url)
        }
    }
    
    @IBAction func signUp(_ sender: Any) {
        let email = txtEmail.getText()
        let password = txtPwd.getText()
        let conPassword = txtConPwd.getText()
        let spoof_phone_number = txtSpoofPhone.getText()
        
        if !email.isValidEmail() {
            self.showWarning("Please enter valid email")
            return
        }
        if password.count < 6 {
            self.showWarning("Password should be at least 6 characters")
            return
        }
        if conPassword != password {
            self.showWarning("Please enter correct confirm password")
            return
        }
        if !spoof_phone_number.isValidePhone() {
            self.showWarning("Please enter valid phone number")
            return
        }
        
        let params: [String: Any] = [
            "email": email.lowercased(),
            "password": password,
            "spoof_phone_number": spoof_phone_number.formatPhoneNumber(),
            "term": self.swtTerm.isOn,
            "is_social": 0
        ]
        
        processSignUp(params: params)
    }
    
    @objc func handleAppleAuth() {
        let spoof_phone_number = txtSpoofPhone.getText()
        if spoof_phone_number.isEmpty() {
            self.showWarning("Please enter my call to phone number")
            return
        }
        
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }
    
    func processSignUp(params: [String: Any]) {
        self.showLoading(self)
        
        API.instance.signUp(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let signUpRes: SignUpRes = response.result.value!
                
                if signUpRes.success! {
                    self.showSuccess(signUpRes.message!)
                    
                    let data = signUpRes.data
                    
                    Store.instance.apiToken = (data?.user?.token)!
                    Store.instance.user = (data?.user)!
                    Store.instance.rememberMe = true
                    
                    self.gotoPageVC(VC_CARD_REGISTER)
                } else {
                    self.showError(signUpRes.message!)
                }
            }
        }
    }
}

extension SignUpVC: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        switch authorization.credential {
        case let appleIDCredential as ASAuthorizationAppleIDCredential:
            let userIdentifier = appleIDCredential.user
            if Store.instance.appleUserId != userIdentifier {
                Store.instance.appleUserId = userIdentifier
                Store.instance.appleUserEmail = appleIDCredential.email!
            }
            
            let spoof_phone_number = txtSpoofPhone.getText()
            if spoof_phone_number.isEmpty() {
                self.showWarning("Please enter my call to phone number")
                return
            }
            
            let params: [String: Any] = [
                "email": Store.instance.appleUserEmail.lowercased(),
                "password": "",
                "spoof_phone_number": spoof_phone_number,
                "term": self.swtTerm.isOn,
                "is_social": 3
            ]
            
            processSignUp(params: params)
        default:
            break
        }
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle error.
        self.showError("Apple Sign Error ->" + error.localizedDescription)
    }
}

extension SignUpVC: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return self.view.window!
    }
}
