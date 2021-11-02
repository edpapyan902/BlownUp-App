//
//  ContactAddVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation
import UIKit
import ContactsUI

class ContactAddVC: BaseVC {
    
    @IBOutlet weak var imgBack: UIImageView!
    @IBOutlet weak var btnAdd: UIButton!
    @IBOutlet weak var txtNumber: TextInput!
    @IBOutlet weak var txtName: TextInput!
    @IBOutlet weak var imgContactAdd: UIImageView!
    @IBOutlet weak var imgAvatar: UIImageView!
    @IBOutlet weak var avatarView: UIView!
    @IBOutlet weak var btnDelete: UIButton!
    
    var currentContact: Contact? = nil
    
    var avatarBase64: String = ""
    
    var imagePicker: ImagePicker!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        initLayout()
    }
    
    func initLayout() {
        self.imgAvatar.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.pickImage)))
        self.imgContactAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onClickContactAdd)))
        self.imgBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onBack)))
        
        self.avatarView.makeRounded(100)
        self.avatarView.makeBorder(1, UIColor.init(named: "colorPrimary")!)
        
        self.imagePicker = ImagePicker(presentationController: self, delegate: self)
        
        if currentContact != nil {
            self.btnAdd.setTitle("UPDATE CONTACT", for: .normal)
            
            self.btnDelete.isHidden = false
            
            self.txtName.setText(currentContact!.name)
            self.txtNumber.setText(currentContact!.number)
            getImageFromUrl(imageView: self.imgAvatar, photoUrl: BASE_SERVER + currentContact!.avatar) { (image) in
                if image != nil {
                    self.imgAvatar.image = image
                }
            }
        } else {
            self.btnDelete.isHidden = true
            self.btnAdd.setTitle("ADD CONTACT", for: .normal)
        }
    }
    
    @objc func onBack() {
        self.currentContact = nil
        self.avatarBase64 = ""
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func onClickContactAdd() {
        self.checkContactPermission()
    }
    
    func checkContactPermission() {
        switch CNContactStore.authorizationStatus(for: CNEntityType.contacts) {
        case .authorized:
            self.openContact()
            break
        case .denied, .notDetermined:
            self.requestContactPermission()
            break
        default: break
        }
    }
    
    func requestContactPermission() {
        let contactStore = CNContactStore()
        contactStore.requestAccess(for: CNEntityType.contacts, completionHandler: { (allowed, error) -> Void in
            if allowed && error == nil {
                self.openContact()
            }
        })
    }
    
    func openContact() {
        DispatchQueue.main.async {
            let contacVC = CNContactPickerViewController()
            contacVC.delegate = self
            self.present(contacVC, animated: true, completion: nil)
        }
    }
    
    @objc func pickImage() {
        self.imagePicker.present(from: self.imgAvatar)
    }
    
    @IBAction func addupdateContact(_ sender: Any) {
        if self.currentContact == nil {
            self.addContact()
        } else {
            self.updateContact()
        }
    }
    
    func addContact() {
        let name = self.txtName.getText()
        let number = self.txtNumber.getText()
        if name.isEmpty() {
            self.showWarning("Please enter contact name")
            return
        }
        if !number.isValidePhone() {
            self.showWarning("Please enter valid contact number")
            return
        }
        
        let params: [String: Any] = [
            "name": name,
            "number": number.formatPhoneNumber(),
            "avatar": avatarBase64
        ]
        
        self.showLoading(self)
        
        API.instance.addContact(params: params){ (response) in
            self.hideLoading()
            
            if response.error == nil {
                let noDataRes: NoDataRes = response.result.value!
                if noDataRes.success {
                    self.showSuccess(noDataRes.message)
                    
                    ContactListVC.instance.loadData()
                    self.onBack()
                } else {
                    self.showError(noDataRes.message)
                }
            }
        }
    }
    
    func updateContact() {
        let name = self.txtName.getText()
        let number = self.txtNumber.getText()
        if name.isEmpty() {
            self.showWarning("Please enter contact name")
            return
        }
        if !number.isValidePhone() {
            self.showWarning("Please enter valid contact number")
            return
        }
        
        let params: [String: Any] = [
            "id": currentContact!.id,
            "name": name,
            "number": number.formatPhoneNumber(),
            "avatar": avatarBase64
        ]
        
        self.showLoading(self)
        
        API.instance.updateContact(params: params){ (response) in
            self.hideLoading()
            
            if response.error == nil {
                let noDataRes: NoDataRes = response.result.value!
                if noDataRes.success {
                    self.showSuccess(noDataRes.message)
                    
                    ContactListVC.instance.loadData()
                    self.onBack()
                } else {
                    self.showError(noDataRes.message)
                }
            }
        }
    }
    
    @IBAction func deleteContact(_ sender: Any) {
        let params: [String: Any] = [
            "id": currentContact!.id
        ]
        
        self.showLoading(self)
        
        API.instance.deleteContact(params: params){ (response) in
            self.hideLoading()
            
            if response.error == nil {
                let noDataRes: NoDataRes = response.result.value!
                if noDataRes.success {
                    self.showSuccess(noDataRes.message)
                    
                    ContactListVC.instance.loadData()
                    self.onBack()
                } else {
                    self.showError(noDataRes.message)
                }
            }
        }
    }
}

extension ContactAddVC: ImagePickerDelegate {
    func didSelect(image: UIImage?) {
        if image != nil {
            self.avatarBase64 = image!.getBase64()
            self.imgAvatar.image = image
        }
    }
}

extension ContactAddVC: CNContactPickerDelegate {
    func contactPicker(_ picker: CNContactPickerViewController, didSelect contact: CNContact) {
        let name = contact.givenName + " " + contact.familyName
        let numbers = contact.phoneNumbers.first
        let phoneNumber = (numbers?.value)?.stringValue ?? ""
        let imageDataAvailable = contact.imageDataAvailable
        let imageData = contact.imageData
        
        self.txtName.setText(name)
        
        if phoneNumber.isValidePhone() {
            self.txtNumber.setText(phoneNumber.formatPhoneNumber())
        }
        
        if imageDataAvailable {
            let avatar = UIImage.init(data: imageData!)
            self.imgAvatar.image = avatar!
            self.avatarBase64 = avatar!.getBase64()
        }
    }
    
    func contactPickerDidCancel(_ picker: CNContactPickerViewController) {
        self.dismiss(animated: true, completion: nil)
    }
}
