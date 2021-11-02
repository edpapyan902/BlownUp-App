//
//  LoginVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import UIKit
import AuthenticationServices

class LoginVC: BaseVC {
    
    @IBOutlet weak var appleAuthProviderView: UIStackView!
    @IBOutlet weak var swtRememberMe: UISwitch!
    @IBOutlet weak var txtPassword: TextInput!
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
    
    @IBAction func goSignUp(_ sender: Any) {
        self.gotoPageVC(VC_SIGN_UP)
    }
    
    @IBAction func goForgetPassword(_ sender: Any) {
        self.gotoPageVC(VC_FORGET_PASSWORD)
    }
    
    @IBAction func login(_ sender: Any) {
        let email = txtEmail.getText()
        let password = txtPassword.getText()
        
        if email.isEmpty() || !email.isValidEmail() {
            self.showWarning("Please enter valid email")
            return
        }
        if password.isEmpty() {
            self.showWarning("Please enter password")
            return
        }
        
        let params: [String: Any] = [
            "email": email.lowercased(),
            "password": password,
            "is_social": 0
        ]
        
        processLogin(params: params)
    }
    
    @objc func handleAppleAuth() {
        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        
        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        authorizationController.delegate = self
        authorizationController.presentationContextProvider = self
        authorizationController.performRequests()
    }
    
    func processLogin(params: [String: Any]) {
        self.showLoading(self)
        
        API.instance.login(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let loginRes: LoginRes = response.result.value!
                
                if loginRes.success! {
                    self.showSuccess(loginRes.message!)
                    
                    let data = loginRes.data
                    
                    Store.instance.apiToken = (data?.user?.token)!
                    Store.instance.user = (data?.user)!
                    Store.instance.rememberMe = self.swtRememberMe.isOn
                    
                    Store.instance.charged = (data?.charged)!
                    
                    if !Store.instance.charged {
                        self.gotoPageVC(VC_CARD_REGISTER)
                    }
                    else {
                        self.gotoPageVC(VC_RECENT_CALL)
                    }
                } else {
                    self.showError(loginRes.message!)
                }
            }
        }
    }
}

extension LoginVC: ASAuthorizationControllerDelegate {
    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        switch authorization.credential {
        case let appleIDCredential as ASAuthorizationAppleIDCredential:
            let userIdentifier = appleIDCredential.user
            if Store.instance.appleUserId != userIdentifier {
                Store.instance.appleUserId = userIdentifier
                Store.instance.appleUserEmail = appleIDCredential.email!
            }
            
            let params: [String: Any] = [
                "email": Store.instance.appleUserEmail.lowercased(),
                "password": "",
                "is_social": 3
            ]
            
            processLogin(params: params)
        default:
            break
        }
    }
    
    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        // Handle error.
        self.showError("Apple Sign Error ->" + error.localizedDescription)
    }
}

extension LoginVC: ASAuthorizationControllerPresentationContextProviding {
    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        return self.view.window!
    }
}
