//
//  Help.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 10/05/2021.
//

import Foundation

struct Help: Codable {
    let id : Int
    let type : Int
    let content : String
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case type = "type"
        case content = "content"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decodeIfPresent(Int.self, forKey: .id)!
        type = try values.decodeIfPresent(Int.self, forKey: .type)!
        content = try values.decodeIfPresent(String.self, forKey: .content)!
    }
}
