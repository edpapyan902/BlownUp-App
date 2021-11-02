//
//  Invoice.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 03/05/2021.
//

import Foundation

struct Invoice: Codable {
    let id : String
    let created : Int64
    let status : String
    let total : Int
    let invoice_pdf : String
    let number : String
    var local_file_path : URL?
    var file_name: String?
    
    enum CodingKeys: String, CodingKey {
        case id = "id"
        case created = "created"
        case status = "status"
        case total = "total"
        case invoice_pdf = "invoice_pdf"
        case number = "number"
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        id = try values.decodeIfPresent(String.self, forKey: .id)!
        created = try values.decodeIfPresent(Int64.self, forKey: .created)!
        status = try values.decodeIfPresent(String.self, forKey: .status)!
        total = try values.decodeIfPresent(Int.self, forKey: .total)!
        invoice_pdf = try values.decodeIfPresent(String.self, forKey: .invoice_pdf)!
        number = try values.decodeIfPresent(String.self, forKey: .number)!
    }
}
