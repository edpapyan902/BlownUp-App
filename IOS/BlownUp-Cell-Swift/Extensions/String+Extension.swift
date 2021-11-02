//
//  String.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation

extension String {
    func isEmpty() -> Bool {
        return self == ""
    }
    
    func splite(_ separatedBy: String) -> [String] {
        return self.components(separatedBy: separatedBy)
    }
    
    func subString(_ start: Int, _ offset: Int) -> String {
        let startIndex = self.index(self.startIndex, offsetBy: start)
        let endIndex = self.index(self.startIndex, offsetBy: (start + offset))
        let range = startIndex..<endIndex
        let result: String = String(self[range])
        return result
    }
    
    func isValidEmail() -> Bool {
        if self.isEmpty() {
            return false
        }
        
        let REGEX = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}"
        return NSPredicate(format: "SELF MATCHES %@", REGEX).evaluate(with: self)
    }
    
    func isValidePhone() -> Bool {
        if self.isEmpty() {
            return false
        }
        
        let phonePattern = #"^\(?\d{3}\)?[ -]?\d{3}[ -]?\d{4}$"#
        let result = self.range(
            of: phonePattern,
            options: .regularExpression
        )

        return result != nil
    }
    
    func formatPhoneNumber() -> String {
        if self.isEmpty() {
            return ""
        }
        
        var phoneNumber = self
        phoneNumber = phoneNumber.replace("(", "")
        phoneNumber = phoneNumber.replace(")", "")
        phoneNumber = phoneNumber.replace("-", "")
        phoneNumber = phoneNumber.replace(" ", "")
        
        var formattedNumber = "("
        formattedNumber = formattedNumber + phoneNumber.subString(0, 3)
        formattedNumber = formattedNumber + ") "
        formattedNumber = formattedNumber + phoneNumber.subString(3, 3)
        formattedNumber = formattedNumber + "-"
        formattedNumber = formattedNumber + phoneNumber.subString(6, 4)
        
        return formattedNumber
    }
    
    func replace(_ to: String, _ by: String) -> String {
        return self.replacingOccurrences(of: to, with: by)
    }
    
    func toUSDateFormat() -> String {
        let result = self.splite("-")
        return result[1] + "/" + result[2] + "/" + result[0]
    }
    
    func ToDictionary() -> [String:Any]? {
        var dictonary:[String:Any]?
        if let data = self.data(using: .utf8) {
            do {
                dictonary = try JSONSerialization.jsonObject(with: data, options: []) as? [String : Any]
                if let myDictionary = dictonary {
                  return myDictionary;
                }
            } catch let error as NSError {
                print(error)
            }
        }
        return dictonary;
    }
    
    func TimeStamp2DateComponents() -> DateComponents {
        let result = self.splite(" ")
        let dayResult = result[0].splite("-")
        let hourResult = result[1].splite(":")
        
        var components = DateComponents()
        components.year = Int(dayResult[0])
        components.month = Int(dayResult[1])
        components.day = Int(dayResult[2])
        components.hour = Int(hourResult[0])
        components.minute = Int(hourResult[1])
        components.second = Int(hourResult[2])
        components.timeZone = .current
        
        return components
    }
}
