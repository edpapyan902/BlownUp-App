//
//  FloatingButton.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 10/05/2021.
//

import Foundation
import UIKit
import MaterialComponents.MaterialButtons

@IBDesignable
class FloatingButton: UIStackView {
    var floatingButton: MDCFloatingButton!
    
    open override func awakeFromNib() {
        super.awakeFromNib()
        
        floatingButton = MDCFloatingButton(shape: .default)
        floatingButton.frame = CGRect(x: 0, y: 0, width: self.frame.size.width, height: self.frame.size.width)
        floatingButton.tintColor = UIColor.white
        floatingButton.setBackgroundColor(UIColor.init(named: "colorPrimary"), for: .normal)
        floatingButton.setImage(UIImage.init(named: "ic_float_add"), for: .normal)
        
        self.addSubview(floatingButton)
    }
    
    func onClicked(_ targetVC: UIViewController, _ action: Selector) {
        floatingButton.addTarget(targetVC, action: action, for: .touchUpInside)
    }
}
