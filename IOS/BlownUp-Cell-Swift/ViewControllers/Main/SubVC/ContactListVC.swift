//
//  ContactListVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation
import UIKit

class ContactListVC: BaseVC {
    
    @IBOutlet weak var imgAdd: UIImageView!
    @IBOutlet weak var btnAdd: FloatingButton!
    @IBOutlet weak var tblContact: UITableView!
    
    var refreshControl : UIRefreshControl!
    var m_Contacts = [Contact]()
    
    static var instance = ContactListVC()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        ContactListVC.instance = self
        
        initLayout()
        loadData()
    }
    
    func initLayout() {
        self.tblContact.delegate = self
        self.tblContact.dataSource = self
        self.tblContact.backgroundColor = UIColor.clear
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.init(named: "colorPrimary")
        
        self.refreshControl.addTarget(self, action: #selector(onRefresh(_:)), for: UIControl.Event.valueChanged)
        
        self.tblContact.addSubview(self.refreshControl)
        
        self.btnAdd.onClicked(self, #selector(onAddClicked))
        
        self.imgAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.goScheduleAdd)))
    }
    
    @objc func onRefresh(_ refreshControl: UIRefreshControl) {
        self.refreshControl?.beginRefreshing()
        initData()
    }
    
    @objc func goScheduleAdd() {
        self.gotoScheduleAddVC(nil)
    }
    
    @objc func onAddClicked() {
        self.gotoContactAddVC(nil)
    }
    
    func loadData() {
        self.showLoading(self)
        initData()
    }
    
    func initData() {
        self.m_Contacts.removeAll()
        self.tblContact.reloadData()
        
        API.instance.getAllContact() {(response) in
            self.hideLoading()
            self.refreshControl?.endRefreshing()
            
            if response.error == nil {
                let contactAllRes: ContactAllRes = response.result.value!
                
                if contactAllRes.success {
                    self.m_Contacts = contactAllRes.data.contacts!
                    
                    if self.m_Contacts.count > 0 {
                        self.tblContact.reloadData()
                    }
                }
            }
        }
    }
}

class ContactTableViewCell: UITableViewCell {
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var lblNumber: UILabel!
    @IBOutlet weak var avatarView: UIView!
    @IBOutlet weak var imgAvatar: UIImageView!
    @IBOutlet weak var loader: UIActivityIndicatorView!
    @IBOutlet weak var mainView: UIView!
}

extension ContactListVC: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.m_Contacts.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.gotoContactAddVC(self.m_Contacts[indexPath.row])
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactItemID", for: indexPath) as! ContactTableViewCell
        let rowIndex = indexPath.row
        let contact = self.m_Contacts[rowIndex]
        
        cell.lblName.text = contact.name
        cell.lblNumber.text = contact.number
        
        cell.avatarView.makeRounded(35)
        cell.avatarView.makeBorder(1, UIColor.init(named: "colorPrimary")!)
        
        cell.loader.isHidden = false
        cell.imgAvatar.isHidden = true
        
        cell.mainView.makeRounded(6)
        cell.mainView.makeBorder(1, UIColor.init(named: "colorGrey")!)
        
        getImageFromUrl(imageView: cell.imgAvatar, photoUrl: BASE_SERVER + contact.avatar) { (image) in
            if image != nil {
                cell.imgAvatar.image = image
                cell.imgAvatar.isHidden = false
                cell.loader.isHidden = true
            }
        }
        
        cell.backgroundColor = UIColor.clear
        cell.isOpaque = false
        
        return cell
    }
}

