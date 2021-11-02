//
//  ScheduleUpdateRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 09/05/2021.
//

import Foundation

struct ScheduleUpdateRes: Codable {
    let success : Bool
    let message : String
    let data : Data

    enum CodingKeys: String, CodingKey {
        case success = "success"
        case message = "message"
        case data = "data"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        success = try values.decodeIfPresent(Bool.self, forKey: .success)!
        message = try values.decodeIfPresent(String.self, forKey: .message)!
        data = try values.decodeIfPresent(Data.self, forKey: .data)!
    }
    
    struct Data: Codable {
        let schedule: Schedule?
        let old_alarm_identify: String?
        
        enum CodingKeys: String, CodingKey {
            case schedule = "schedule"
            case old_alarm_identify = "old_alarm_identify"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            schedule = try values.decodeIfPresent(Schedule.self, forKey: .schedule)
            old_alarm_identify = try values.decodeIfPresent(String.self, forKey: .old_alarm_identify)
        }
    }
}
