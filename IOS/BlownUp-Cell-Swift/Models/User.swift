//
//  User.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation

struct User: Codable {
    let id : Int?
    let name : String?
    let email : String?
    let avatar : String?
    let iphone_device_token : String?
    let real_phone_number : String?
    let spoof_phone_number : String?
    let terms : Int?
    let active : Int?
    let is_social : Int?
    var token : String?
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case name = "name"
        case email = "email"
        case avatar = "avatar"
        case iphone_device_token = "iphone_device_token"
        case real_phone_number = "real_phone_number"
        case spoof_phone_number = "spoof_phone_number"
        case terms = "terms"
        case active = "active"
        case is_social = "is_social"
        case token = "token"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decodeIfPresent(Int.self, forKey: .id)
        name = try values.decodeIfPresent(String.self, forKey: .name)
        email = try values.decodeIfPresent(String.self, forKey: .email)
        avatar = try values.decodeIfPresent(String.self, forKey: .avatar)
        iphone_device_token = try values.decodeIfPresent(String.self, forKey: .iphone_device_token)
        real_phone_number = try values.decodeIfPresent(String.self, forKey: .real_phone_number)
        spoof_phone_number = try values.decodeIfPresent(String.self, forKey: .spoof_phone_number)
        terms = try values.decodeIfPresent(Int.self, forKey: .terms)
        active = try values.decodeIfPresent(Int.self, forKey: .active)
        is_social = try values.decodeIfPresent(Int.self, forKey: .is_social)
        token = try values.decodeIfPresent(String.self, forKey: .token)
    }
}
