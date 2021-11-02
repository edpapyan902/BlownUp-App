//
//  ScheduleAddVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation
import UIKit
import DatePicker

class ScheduleAddVC: BaseVC {
    
    var selectedContact: Contact? = nil
    var currentSchedule: Schedule? = nil
    
    var selectedDate: String = ""
    
    @IBOutlet weak var datePicker: UIDatePicker!
    @IBOutlet weak var imgBack: UIImageView!
    @IBOutlet weak var btnPickDate: UIButton!
    @IBOutlet weak var btnAdd: UIButton!
    @IBOutlet weak var imgContact: UIImageView!
    @IBOutlet weak var txtNumber: TextInput!
    
    static var instance = ScheduleAddVC()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        ScheduleAddVC.instance = self
        
        initLayout()
    }
    
    func initLayout() {
        self.imgContact.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.showContactDialog)))
        self.imgBack.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onBack)))
        
        datePicker.setValue(UIColor(named: "colorBlue"), forKey: "textColor")
        
        if currentSchedule == nil {
            let today = Date()
            self.selectedDate = today.toString("yyyy-MM-dd")
            self.btnPickDate.setTitle(today.toString("MM/dd/yyyy"), for: .normal)
            
            self.btnAdd.setTitle("ADD SCHEDULE", for: .normal)
        } else {
            self.selectedContact = currentSchedule?.contact
            let dateResult = currentSchedule?.scheduled_at.splite(" ")
            self.selectedDate = dateResult![0]
            
            self.btnPickDate.setTitle(selectedDate.toUSDateFormat(), for: .normal)
            
            let phoneNumber = (currentSchedule?.contact == nil ? currentSchedule?.number : currentSchedule?.contact?.number)!
            if phoneNumber.isValidePhone() {
                self.txtNumber.setText(phoneNumber.formatPhoneNumber())
            }
            
            let timeResult = dateResult![1].splite(":")
            let date = Calendar.current.date(bySettingHour: Int(timeResult[0])!, minute: Int(timeResult[1])!, second: 0, of: Date())!
            self.datePicker.setDate(date, animated: true)
            
            self.btnAdd.setTitle("UPDATE SCHEDULE", for: .normal)
        }
    }
    
    @objc func showContactDialog() {
        self.gotoModalVC(VC_DIALOG_CONTACT, true)
    }
    
    @objc func onBack() {
        self.currentSchedule = nil
        self.selectedContact = nil
        
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func pickDate(_ sender: UIButton) {
        let minDate = DatePickerHelper.shared.dateFrom(day: 01, month: 01, year: 1970)!
        let maxDate = DatePickerHelper.shared.dateFrom(day: 31, month: 12, year: 2050)!
        let today = Date()
        let datePicker = DatePicker()
        datePicker.setup(beginWith: today, min: minDate, max: maxDate) { (selected, date) in
            if selected, let selectedDate = date {
                self.selectedDate = selectedDate.toString("yyyy-MM-dd")
                self.btnPickDate.setTitle(selectedDate.toString("MM/dd/yyyy"), for: .normal)
            }
        }
        datePicker.show(in: self)
    }
    
    func setContact(_ contact: Contact) {
        self.selectedContact = contact
        if contact.number.isValidePhone() {
            self.txtNumber.setText(contact.number.formatPhoneNumber())
        }
    }
    
    @IBAction func addupdateSchedule(_ sender: Any) {
        if self.currentSchedule == nil {
            self.addSchedule()
        } else {
            self.updateSchedule()
        }
    }
    
    func addSchedule() {
        var number = self.txtNumber.getText()
        if !number.isValidePhone() {
            self.showWarning("Please enter vaild phone number.")
            return
        }
        
        let date = self.datePicker.date
        let components = Calendar.current.dateComponents([.hour, .minute], from: date)
        let scheduled_at = selectedDate + " " + PLUS0(components.hour!) + ":" + PLUS0(components.minute!) + ":00"
        
        var n_id_contact = 0
        if selectedContact != nil && selectedContact?.number == number {
            n_id_contact = selectedContact!.id
            number = ""
        }
        
        let params: [String: Any] = [
            "n_id_contact": n_id_contact,
            "number": number.formatPhoneNumber(),
            "scheduled_at": localToEST(scheduled_at)
        ]
        
        self.showLoading(self)
        
        API.instance.addSchedule(params: params){ [self] (response) in
            self.hideLoading()
            
            if response.error == nil {
                let scheduleAddRes: ScheduleAddRes = response.result.value!
                if scheduleAddRes.success {
                    self.showSuccess(scheduleAddRes.message)
                    
                    ScheduleListVC.instance.loadData()
                    
                    self.onBack()
                } else {
                    self.showError(scheduleAddRes.message)
                }
            }
        }
    }
    
    func updateSchedule() {
        var number = self.txtNumber.getText()
        if !number.isValidePhone() {
            self.showWarning("Please enter vaild phone number.")
            return
        }
        
        let date = self.datePicker.date
        let components = Calendar.current.dateComponents([.hour, .minute], from: date)
        let scheduled_at = selectedDate + " " + PLUS0(components.hour!) + ":" + PLUS0(components.minute!) + ":00"
        
        var n_id_contact = 0
        if selectedContact != nil && selectedContact?.number == number {
            n_id_contact = selectedContact!.id
            number = ""
        }
        
        let params: [String: Any] = [
            "id": self.currentSchedule?.id,
            "n_id_contact": n_id_contact,
            "number": number.formatPhoneNumber(),
            "scheduled_at": getRelativeTime(scheduled_at, true)
        ]
        
        self.showLoading(self)
        
        API.instance.updateSchedule(params: params){ (response) in
            self.hideLoading()
            
            if response.error == nil {
                let scheduleUpdateRes: ScheduleUpdateRes = response.result.value!
                if scheduleUpdateRes.success {
                    self.showSuccess(scheduleUpdateRes.message)

                    ScheduleListVC.instance.loadData()
                    self.onBack()
                } else {
                    self.showError(scheduleUpdateRes.message)
                }
            }
        }
    }
}
