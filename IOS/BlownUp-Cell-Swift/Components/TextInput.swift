//
//  MaterialTextInputField.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import UIKit
import MaterialComponents.MaterialTextControls_OutlinedTextFields

@IBDesignable
class TextInput: UIStackView {
    
    var textField: MDCOutlinedTextField!
    
    @IBInspectable var text: String!
    @IBInspectable var placeHolder: String!
    @IBInspectable var hint: String!
    @IBInspectable var outlineActiveColor: UIColor! = UIColor.init(named: "colorPrimary")
    @IBInspectable var outlineNormalColor: UIColor! = UIColor.init(named: "colorHeavyGrey")
    
    @IBInspectable var isPassword: Bool = false
    @IBInspectable var isPhoneNumber: Bool = false
    
    open override func awakeFromNib() {
        super.awakeFromNib()
        
        textField = MDCOutlinedTextField(frame: CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.width))
        textField.text = text
        textField.placeholder = placeHolder
        textField.label.text = hint
        textField.isSecureTextEntry = isPassword
        
        textField.setTextColor(UIColor.black, for: MDCTextControlState.normal)
        textField.setTextColor(UIColor.black, for: MDCTextControlState.editing)
        
        textField.setNormalLabelColor(outlineNormalColor, for: MDCTextControlState.normal)
        textField.setNormalLabelColor(outlineNormalColor, for: MDCTextControlState.editing)
        
        textField.setFloatingLabelColor(outlineNormalColor, for: MDCTextControlState.normal)
        textField.setFloatingLabelColor(outlineActiveColor, for: MDCTextControlState.editing)
        
        textField.setOutlineColor(outlineNormalColor, for: MDCTextControlState.normal)
        textField.setOutlineColor(outlineActiveColor, for: MDCTextControlState.editing)
        
        if isPhoneNumber {
            textField.delegate = self
        }
        
        self.addArrangedSubview(textField)
    }
    
    func getText() -> String {
        return textField.text!
    }
    
    func setText(_ text: String) {
        textField.text = text
    }
    
    func setEnabled(_ enabled: Bool) {
        textField.isEnabled = enabled
    }
    
    func clearFocus() {
        textField.endEditing(true)
    }
    
    func checkEnglishPhoneNumberFormat(_ string: String?, _ str: String?) -> Bool {
        if string == "" {
            return true
        } else if str!.count < 3 {
            if str!.count == 1 {
                textField.text = "("
            }
        } else if str!.count == 5 {
            textField.text = textField.text! + ") "
        } else if str!.count == 10 {
            textField.text = textField.text! + "-"
        } else if str!.count > 14 {
            return false
        }
        return true
    }
}

extension TextInput: UITextFieldDelegate {
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
        let str = (textField.text! as NSString).replacingCharacters(in: range, with: string)
        return checkEnglishPhoneNumberFormat(string, str)
    }
}
