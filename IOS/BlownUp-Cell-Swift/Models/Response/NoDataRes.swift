//
//  CardAddRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 02/05/2021.
//

import Foundation

struct NoDataRes : Codable {
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
        let any: String?
        
        enum CodingKeys: String, CodingKey {
            case any = "any"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            any = try values.decodeIfPresent(String.self, forKey: .any)
        }
    }
}
