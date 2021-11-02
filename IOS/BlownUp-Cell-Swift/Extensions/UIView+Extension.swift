//
//  UIView.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 08/05/2021.
//

import Foundation
import UIKit

extension UIView {
    
    func makeRounded(_ radius: CGFloat) {
        self.layer.masksToBounds = false
        if radius == 0 {
            self.layer.cornerRadius = self.frame.width / 2
        }
        else {
            self.layer.cornerRadius = radius
        }
        self.clipsToBounds = true
        self.contentMode = .scaleToFill
    }
    
    func makeBorder(_ width: CGFloat, _ color: UIColor) {
        self.layer.borderWidth = width
        self.layer.borderColor = color.cgColor
    }
}
