//
//  ChargeRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 02/06/2021.
//

import Foundation

struct ChargeRes : Codable {
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
        let client_secret : String?
        
        enum CodingKeys: String, CodingKey {
            case any = "any"
            case client_secret = "client_secret"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            any = try values.decodeIfPresent(String.self, forKey: .any)
            client_secret = try values.decodeIfPresent(String.self, forKey: .client_secret)
        }
    }
}

