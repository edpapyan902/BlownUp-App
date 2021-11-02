//
//  Contact.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 04/05/2021.
//

import Foundation

struct Contact: Codable {
    let id : Int
    let name : String
    let avatar : String
    let number : String
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case name = "name"
        case avatar = "avatar"
        case number = "number"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decodeIfPresent(Int.self, forKey: .id)!
        name = try values.decodeIfPresent(String.self, forKey: .name)!
        avatar = try values.decodeIfPresent(String.self, forKey: .avatar)!
        number = try values.decodeIfPresent(String.self, forKey: .number)!
    }
}
