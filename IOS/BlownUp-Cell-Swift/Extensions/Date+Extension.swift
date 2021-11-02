//
//  Date.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation

extension Date {

    func toString(_ format: String = "yyyy-MM-dd") -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        return dateFormatter.string(for: self)!
    }
    
    func addDay(_ value: Int = 1) -> Date {
        return Calendar.current.date(byAdding: .day, value: value, to: self)!
    }
    
    func addSecond(_ value: Int) -> Date {
        return Calendar.current.date(byAdding: .second, value: value, to: self)!
    }
    
    func int2date(milliseconds: Int64) -> Date {
        return Date(timeIntervalSince1970: TimeInterval(milliseconds))
    }
}
