//
//  UIImage.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 10/05/2021.
//

import Foundation
import UIKit

extension UIImage {
    func getBase64() -> String {
        let imageData = self.pngData()!
        return imageData.base64EncodedString(options: Data.Base64EncodingOptions.lineLength64Characters)
    }
}
