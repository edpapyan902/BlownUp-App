//
//  CardRegisterVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 28/04/2021.
//

import Foundation
import UIKit
import Stripe
import PassKit

class CardRegisterVC: BaseVC {
    
    @IBOutlet weak var applePayProvider: UIStackView!
    @IBOutlet weak var creditCardProvider: CreditCardView!
    let applePayButton: PKPaymentButton = PKPaymentButton(paymentButtonType: .plain, paymentButtonStyle: .black)
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        initLayout()
    }
    
    func initLayout() {
        applePayButton.isHidden = !StripeAPI.deviceSupportsApplePay()
        applePayButton.addTarget(self, action: #selector(handleApplePayButtonTapped), for: .touchUpInside)
        self.applePayProvider.addArrangedSubview(applePayButton)
    }
    
    @objc func handleApplePayButtonTapped() {
        let paymentRequest = StripeAPI.paymentRequest(withMerchantIdentifier: APPLE_MERCHANT_ID, country: "US", currency: "USD")
        
        // Configure the line items on the payment request
        paymentRequest.paymentSummaryItems = [
            PKPaymentSummaryItem(label: "Get Blow Yourself UP", amount: 9.99),
        ]
        
        // Initialize an STPApplePayContext instance
        if let applePayContext = STPApplePayContext(paymentRequest: paymentRequest, delegate: self) {
            // Present Apple Pay payment sheet
            applePayContext.presentApplePay(completion: nil)
        } else {
            // There is a problem with your Apple Pay configuration
            print("There is a problem with your Apple Pay configuration")
        }
    }
    
    @IBAction func registerCard(_ sender: Any) {
        if !creditCardProvider.getCardView().isValid {
            self.showWarning("Please enter valid card infomation")
            return
        }
        
        let paymentMethodParams = STPPaymentMethodParams(
            card: creditCardProvider.getCardView().cardParams,
            billingDetails: STPPaymentMethodBillingDetails(),
            metadata: nil
        )
        createPaymentMethod(paymentMethodParams: paymentMethodParams, isApplePay: false)
    }
    
    func createPaymentMethod(paymentMethodParams: STPPaymentMethodParams, isApplePay: Bool) {
        self.showLoading(self)
        
        STPAPIClient.shared.createPaymentMethod(
            with: paymentMethodParams,
            completion: { paymentMethod, error in
                DispatchQueue.main.async {
                    if paymentMethod != nil {
                        self.processCharge(paymentMethod: (paymentMethod?.stripeId)!)
                    } else {
                        self.showWarning("This payment method can't use for charging. Please try again with vaild card.")
                        self.hideLoading()
                    }
                }
            })
    }
    
    func processCharge(paymentMethod: String) {
        let params: [String: Any] = [
            "payment_method": paymentMethod
        ]
        
        API.instance.charge(params: params) { (response) in
            self.hideLoading()
            
            if response.error == nil {
                let chargeRes: ChargeRes = response.result.value!
                
                if chargeRes.success {
                    Store.instance.charged = true
                    self.showSuccess(chargeRes.message)
                    self.gotoPageVC(VC_SUCCESS)
                } else {
                    self.showWarning(chargeRes.message)
                }
            }
        }
    }
    
    func processChargeWithApple(paymentMethod: String, completion: @escaping STPIntentClientSecretCompletionBlock) {
        let params: [String: Any] = [
            "payment_method": paymentMethod
        ]
        
        API.instance.charge(params: params) { (response) in
            if response.error == nil {
                let chargeRes: ChargeRes = response.result.value!
                
                if chargeRes.success {
                    Store.instance.charged = true
                    completion(chargeRes.data.client_secret, nil)
                } else {
                    self.showWarning(chargeRes.message)
                }
            }
        }
    }
}

extension CardRegisterVC: STPApplePayContextDelegate {
    func applePayContext(_ context: STPApplePayContext, didCreatePaymentMethod paymentMethod: STPPaymentMethod, paymentInformation: PKPayment, completion: @escaping STPIntentClientSecretCompletionBlock) {
        
        self.processChargeWithApple(paymentMethod: paymentMethod.stripeId, completion: completion)
    }
    
    func applePayContext(_ context: STPApplePayContext, didCompleteWith status: STPPaymentStatus, error: Error?) {
        switch status {
        case .success:
            // Payment succeeded, show a receipt view
            self.gotoPageVC(VC_SUCCESS)
            break
        case .error:
            // Payment failed, show the error
            break
        case .userCancellation:
            // User cancelled the payment
            break
        default:
            break
        }
    }
}
