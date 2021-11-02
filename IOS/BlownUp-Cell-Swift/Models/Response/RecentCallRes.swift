//
//  RecentCallRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 04/05/2021.
//

import Foundation

struct RecentCallRes: Codable {
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
        let schedules: [Schedule]?
        
        enum CodingKeys: String, CodingKey {
            case schedules = "schedules"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            schedules = try values.decodeIfPresent([Schedule].self, forKey: .schedules)
        }
    }
}
