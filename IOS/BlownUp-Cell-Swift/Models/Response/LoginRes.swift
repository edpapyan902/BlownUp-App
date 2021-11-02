//
//  LoginRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation

struct LoginRes : Codable {
    let success : Bool?
    let message : String?
    let data : Data?

    enum CodingKeys: String, CodingKey {
        case success = "success"
        case message = "message"
        case data = "data"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        success = try values.decodeIfPresent(Bool.self, forKey: .success)
        message = try values.decodeIfPresent(String.self, forKey: .message)
        data = try values.decodeIfPresent(Data.self, forKey: .data)
    }
    
    struct Data: Codable {
        let user: User?
        let charged: Bool?
        
        enum CodingKeys: String, CodingKey {
            case user = "user"
            case charged = "charged"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            user = try values.decodeIfPresent(User.self, forKey: .user)
            charged = try values.decodeIfPresent(Bool.self, forKey: .charged)
        }
    }
}
