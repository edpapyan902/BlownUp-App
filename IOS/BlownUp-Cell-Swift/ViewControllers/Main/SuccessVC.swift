//
//  SuccessVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 02/05/2021.
//

import Foundation
import UIKit

class SuccessVC: BaseVC {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    @IBAction func goHome(_ sender: Any) {
        self.gotoPageVC(VC_RECENT_CALL)
    }
}
