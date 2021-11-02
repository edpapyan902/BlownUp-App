//
//  RecentCallVC.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 28/04/2021.
//

import Foundation
import UIKit

class RecentCallVC: BaseVC {
    
    @IBOutlet weak var tblRecentCall: UITableView!
    var refreshControl : UIRefreshControl!
    
    var m_Schedules = [Schedule]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        initLayout()
        
        self.showLoading(self)
        
        updateDeviceToken()
        
        initData()
    }
    
    func updateDeviceToken() {
        if !Store.instance.voipToken.isEmpty() {
            let params: [String: Any] = [
                "platform": "ios",
                "device_token": Store.instance.voipToken
            ]
            API.instance.updateDeviceToken(params: params) { (response) in}
        }
    }
    
    func initLayout() {
        self.setStatusBarStyle(true)
        
        self.tblRecentCall.delegate = self
        self.tblRecentCall.dataSource = self
        self.tblRecentCall.backgroundColor = UIColor.clear
        
        self.refreshControl = UIRefreshControl()
        self.refreshControl.backgroundColor = UIColor.clear
        self.refreshControl.tintColor = UIColor.white
        
        self.refreshControl.addTarget(self, action: #selector(onRefresh(_:)), for: UIControl.Event.valueChanged)
        
        self.tblRecentCall.addSubview(self.refreshControl)
    }
    
    @objc func onRefresh(_ refreshControl: UIRefreshControl) {
        self.refreshControl?.beginRefreshing()
        initData()
    }
    
    @IBAction func goAddSchedule(_ sender: Any) {
        self.gotoMainVC(1)
    }
    
    @IBAction func goScheduleList(_ sender: Any) {
        self.gotoMainVC(2)
    }
    
    @IBAction func goMyAccount(_ sender: Any) {
        self.gotoMainVC(3)
    }
    
    func initData() {
        self.m_Schedules.removeAll()
        self.tblRecentCall.reloadData()
        
        API.instance.getAllRecentCall() {(response) in
            self.hideLoading()
            self.refreshControl?.endRefreshing()
            
            if response.error == nil {
                let recentCallRes: RecentCallRes = response.result.value!
                
                if recentCallRes.success {
                    self.m_Schedules = recentCallRes.data.schedules!
                    if self.m_Schedules.count > 0 {
                        self.tblRecentCall.reloadData()
                    }
                }
            }
        }
    }
}

class RecentCallTableViewCell: UITableViewCell {
    @IBOutlet weak var lblDate: UILabel!
    @IBOutlet weak var lblTime: UILabel!
    @IBOutlet weak var lblNumber: UILabel!
}

extension RecentCallVC: UITableViewDataSource, UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.m_Schedules.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "RecentCallItemID", for: indexPath) as! RecentCallTableViewCell
        let rowIndex = indexPath.row
        let schedule = self.m_Schedules[rowIndex]
        
        let scheduled_at = estToLocal(schedule.scheduled_at)
        let dateResult = scheduled_at.splite(" ")
        let timeResult = dateResult[1].splite(":")
        
        let date = Calendar.current.date(bySettingHour: Int(timeResult[0])!, minute: Int(timeResult[1])!, second: 0, of: Date())!
        
        cell.lblDate.text = dateResult[0].toUSDateFormat()
        cell.lblTime.text = date.toString("h:mm a")
        cell.lblNumber.text = schedule.contact != nil ? schedule.contact?.number : schedule.number
        
        cell.backgroundColor = UIColor.clear
        cell.isOpaque = false
        
        return cell
    }
}
