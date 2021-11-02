//
//  Schedule.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 04/05/2021.
//

import Foundation

struct Schedule: Codable {
    let id : Int
    let number : String?
    let scheduled_at : String
    let alarm_identify : String
    var contact : Contact? = nil
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case number = "number"
        case scheduled_at = "scheduled_at"
        case alarm_identify = "alarm_identify"
        case contact = "contact"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decodeIfPresent(Int.self, forKey: .id)!
        number = try values.decodeIfPresent(String.self, forKey: .number)
        scheduled_at = try values.decodeIfPresent(String.self, forKey: .scheduled_at)!
        alarm_identify = try values.decodeIfPresent(String.self, forKey: .alarm_identify)!
        contact = try values.decodeIfPresent(Contact.self, forKey: .contact)
    }
}
