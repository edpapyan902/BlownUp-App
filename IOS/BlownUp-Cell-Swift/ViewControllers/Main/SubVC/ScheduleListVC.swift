//
//  ScheduleListVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation
import UIKit

class ScheduleListVC: BaseVC {
    
    @IBOutlet weak var imgAdd: UIImageView!
    @IBOutlet weak var btnAdd: FloatingButton!
    @IBOutlet weak var tblSchedule: UITableView!
    var refreshControl : UIRefreshControl!
    
    var m_Schedules = [Schedule]()
    
    static var instance = ScheduleListVC()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        ScheduleListVC.instance = self
        
        initLayout()
        loadData()
    }
    
    func initLayout() {
        self.tblSchedule.delegate = self
        self.tblSchedule.dataSource = self
        self.tblSchedule.backgroundColor = UIColor.clear
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.init(named: "colorPrimary")
        
        self.refreshControl.addTarget(self, action: #selector(onRefresh(_:)), for: UIControl.Event.valueChanged)
        
        self.tblSchedule.addSubview(self.refreshControl)
        
        self.btnAdd.onClicked(self, #selector(onAddClicked))
        
        self.imgAdd.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onAddClicked)))
    }
    
    @objc func onAddClicked() {
        self.gotoScheduleAddVC(nil)
    }
    
    @objc func onRefresh(_ refreshControl: UIRefreshControl) {
        self.refreshControl?.beginRefreshing()
        getData()
    }
    
    func loadData() {
        self.showLoading(self)
        getData()
    }
    
    func getData() {
        self.m_Schedules.removeAll()
        self.tblSchedule.reloadData()
        
        API.instance.getAllSchedule() {(response) in
            self.hideLoading()
            self.refreshControl?.endRefreshing()
            
            if response.error == nil {
                let scheduleAllRes: ScheduleAllRes = response.result.value!
                
                if scheduleAllRes.success {
                    self.m_Schedules = scheduleAllRes.data.schedules!
                    if self.m_Schedules.count > 0 {
                        self.tblSchedule.reloadData()
                    }
                }
            }
        }
    }
    
    @objc func onItemDelete(_ sender: UITapGestureRecognizer) {
        let imgAvatar = sender.view as! UIImageView
        let params: [String: Any] = [
            "id": self.m_Schedules[imgAvatar.tag].id
        ]
        
        self.showLoading(self)
        
        API.instance.deleteSchedule(params: params){ (response) in
            self.hideLoading()
            
            if response.error == nil {
                let noDataRes: NoDataRes = response.result.value!
                if noDataRes.success {
                    self.showSuccess(noDataRes.message)
                    
                    self.m_Schedules.remove(at: imgAvatar.tag)
                    self.tblSchedule.reloadData()
                } else {
                    self.showError(noDataRes.message)
                }
            }
        }
    }
}

class ScheduleTableViewCell: UITableViewCell {
    @IBOutlet weak var lblName: UILabel!
    @IBOutlet weak var lblDate: UILabel!
    @IBOutlet weak var lblTime: UILabel!
    @IBOutlet weak var lblNumber: UILabel!
    @IBOutlet weak var avatarView: UIView!
    @IBOutlet weak var loader: UIActivityIndicatorView!
    @IBOutlet weak var imgAvatar: UIImageView!
    @IBOutlet weak var imgDelete: UIImageView!
    @IBOutlet weak var contactView: UIView!
    @IBOutlet weak var mainView: UIView!
    @IBOutlet weak var timeViewTopConstraints: NSLayoutConstraint!
    @IBOutlet weak var mainViewHeightConstraints: NSLayoutConstraint!
}

extension ScheduleListVC: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.m_Schedules.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.gotoScheduleAddVC(self.m_Schedules[indexPath.row])
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ScheduleItemID", for: indexPath) as! ScheduleTableViewCell
        let rowIndex = indexPath.row
        let schedule = self.m_Schedules[rowIndex]
        
        let scheduled_at = estToLocal(schedule.scheduled_at)
        let dateResult = scheduled_at.splite(" ")
        let timeResult = dateResult[1].splite(":")
        
        let date = Calendar.current.date(bySettingHour: Int(timeResult[0])!, minute: Int(timeResult[1])!, second: 0, of: Date())!
        
        cell.lblDate.text = dateResult[0].toUSDateFormat()
        cell.lblTime.text = date.toString("h:mm a")
        
        cell.mainView.makeRounded(8)
        cell.mainView.makeBorder(1, UIColor.init(named: "colorGrey")!)
        
        if schedule.contact != nil {
            cell.lblName.text = schedule.contact!.name
            cell.lblNumber.text = schedule.contact!.number
            
            cell.avatarView.makeRounded(35)
            cell.avatarView.makeBorder(1, UIColor.init(named: "colorPrimary")!)
            cell.loader.isHidden = false
            cell.imgAvatar.isHidden = true
            
            getImageFromUrl(imageView: cell.imgAvatar, photoUrl: BASE_SERVER + schedule.contact!.avatar) { (image) in
                if image != nil {
                    cell.imgAvatar.image = image
                    cell.imgAvatar.isHidden = false
                    cell.loader.isHidden = true
                }
            }
            
            cell.contactView.isHidden = false
            cell.mainViewHeightConstraints.constant = 130
            cell.timeViewTopConstraints.constant = 85
            cell.mainView.layoutIfNeeded()
        } else {
            cell.lblNumber.text = schedule.number
            
            cell.contactView.isHidden = true
            cell.mainViewHeightConstraints.constant = 60
            cell.timeViewTopConstraints.constant = 12
            cell.mainView.layoutIfNeeded()
        }
        
        cell.imgDelete.tag = rowIndex
        
        cell.imgDelete.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(self.onItemDelete(_:))))
        
        cell.backgroundColor = UIColor.clear
        cell.isOpaque = false
        
        return cell
    }
}
