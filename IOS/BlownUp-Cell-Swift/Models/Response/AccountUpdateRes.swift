//
//  AccountUpdateRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 15/05/2021.
//

import Foundation

struct AccountUpdateRes: Codable {
    let password_success : Bool
    let spoof_phone_success : Bool
    let message : String
    let data : Data

    enum CodingKeys: String, CodingKey {
        case password_success = "password_success"
        case spoof_phone_success = "spoof_phone_success"
        case message = "message"
        case data = "data"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        password_success = try values.decodeIfPresent(Bool.self, forKey: .password_success)!
        spoof_phone_success = try values.decodeIfPresent(Bool.self, forKey: .spoof_phone_success)!
        message = try values.decodeIfPresent(String.self, forKey: .message)!
        data = try values.decodeIfPresent(Data.self, forKey: .data)!
    }
    
    struct Data: Codable {
        let user : User?
        let user_access_token : String?
        
        enum CodingKeys: String, CodingKey {
            case user = "user"
            case user_access_token = "user_access_token"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            user = try values.decodeIfPresent(User.self, forKey: .user)
            user_access_token = try values.decodeIfPresent(String.self, forKey: .user_access_token)
        }
    }
}
