//
//  HelpVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation
import UIKit

class HelpVC: BaseVC {
    
    static var instance: HelpVC? = nil
    
    @IBOutlet weak var imgScheduleAdd: UIImageView!
    @IBOutlet weak var tblHelp: UITableView!
    
    var m_Helps = [Help]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        HelpVC.instance = self
        
        initLayout()
        
        initData()
    }
    
    func initLayout() {
        self.tblHelp.delegate = self
        self.tblHelp.dataSource = self
        self.tblHelp.backgroundColor = UIColor.clear
        
        self.imgScheduleAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onScheduleAddClicked)))
    }
    
    @IBAction func visitWebsite(_ sender: Any) {
        if let url = URL(string: APP_LANDING_URL), UIApplication.shared.canOpenURL(url) {
            UIApplication.shared.openURL(url)
        }
    }
    
    @objc func onScheduleAddClicked() {
        self.gotoScheduleAddVC(nil)
    }
    
    func initData() {
        self.showLoading(self)
        
        self.m_Helps.removeAll()
        self.tblHelp.reloadData()
        
        API.instance.getAllHelp() {(response) in
            self.hideLoading()
            
            if response.error == nil {
                let helpRes: HelpRes = response.result.value!
                
                if helpRes.success {
                    let helps = helpRes.data.helps!
                    
                    if helps.count > 0 {
                        for help in helps {
                            if help.type == 1 {
                                self.m_Helps.append(help)
                            }
                        }
                        
                        self.tblHelp.reloadData()
                    }
                }
            }
        }
    }
}

class HelpTableViewCell: UITableViewCell {
    @IBOutlet weak var lblDescription: UILabel!
}

extension HelpVC: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.m_Helps.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "HelpItemID", for: indexPath) as! HelpTableViewCell
        let rowIndex = indexPath.row
        
        let help = self.m_Helps[rowIndex]
        
        cell.lblDescription.text = help.content
        
        cell.backgroundColor = UIColor.clear
        cell.isOpaque = false
        
        return cell
    }
}
