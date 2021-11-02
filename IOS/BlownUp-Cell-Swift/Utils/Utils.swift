//
//  Utils.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 08/05/2021.
//

import Foundation
import UIKit
import Kingfisher

func getImageFromUrl(imageView: UIImageView, photoUrl: String, completion: @escaping ( _ response: UIImage?) -> Void) -> Void {
    guard let url = URL.init(string: photoUrl) else {
        return
    }
    imageView.kf.setImage(
        with: url,
        options: [
            .cacheOriginalImage
        ])
    {
        result in
        switch result {
        case .success(let value):
            completion(value.image)
        case .failure(let error):
            print("Image loader error:\(error)")
            print("Image URL======>\(photoUrl)")
            completion(nil)
        }
    }
}

func PLUS0(_ value: Int) -> String {
    return value < 10 ? "0" + String(value) : String (value)
}

func localToEST(_ dateStr: String) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd H:mm:ss"
    dateFormatter.calendar = Calendar.current
    dateFormatter.timeZone = TimeZone.current
    
    if let date = dateFormatter.date(from: dateStr) {
        dateFormatter.timeZone = TimeZone(abbreviation: "EST")
        dateFormatter.dateFormat = "yyyy-MM-dd H:mm:ss"
        
        return dateFormatter.string(from: date)
    }
    
    return ""
}

func estToLocal(_ dateStr: String) -> String {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd H:mm:ss"
    dateFormatter.calendar = Calendar.current
    dateFormatter.timeZone = TimeZone(abbreviation: "EST")
    
    if let date = dateFormatter.date(from: dateStr) {
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.dateFormat = "yyyy-MM-dd H:mm:ss"
        
        return dateFormatter.string(from: date)
    }
    
    return ""
}
