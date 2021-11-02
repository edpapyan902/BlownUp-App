//
//  ForgetPasswordRes.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 17/05/2021.
//

import Foundation

struct ForgetPasswordRes: Codable {
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
        let verify_code: Int
        
        enum CodingKeys: String, CodingKey {
            case verify_code = "verify_code"
        }
        
        init(from decoder: Decoder) throws {
            let values = try decoder.container(keyedBy: CodingKeys.self)
            verify_code = try values.decodeIfPresent(Int.self, forKey: .verify_code)!
        }
    }
}
