//
//  API.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import Alamofire
import AlamofireMapper

class API {
    
    static let instance = API()
    
    func login(params: [String: Any], completion: @escaping ( _ response: DataResponse<LoginRes>) -> Void) -> Void {
        Alamofire.request(URL_LOGIN, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<LoginRes>) in
            completion(response)
        }
    }
    
    func signUp(params: [String: Any], completion: @escaping ( _ response: DataResponse<SignUpRes>) -> Void) -> Void {
        Alamofire.request(URL_SIGN_UP, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<SignUpRes>) in
            completion(response)
        }
    }
    
    func forgetPassword(params: [String: Any], completion: @escaping ( _ response: DataResponse<ForgetPasswordRes>) -> Void) -> Void {
        Alamofire.request(URL_FORGET_PASSWORD, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ForgetPasswordRes>) in
            completion(response)
        }
    }
    
    func resetPassword(params: [String: Any], completion: @escaping ( _ response: DataResponse<ResetPasswordRes>) -> Void) -> Void {
        Alamofire.request(URL_RESET_PASSWORD, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ResetPasswordRes>) in
            completion(response)
        }
    }
    
    func getChargeStatus(completion: @escaping ( _ response: DataResponse<ChargeStatusRes>) -> Void) -> Void {
        Alamofire.request(URL_CHARGE_STATUS, method: .get, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ChargeStatusRes>) in
            completion(response)
        }
    }
    
    func charge(params: [String: Any], completion: @escaping ( _ response: DataResponse<ChargeRes>) -> Void) -> Void {
        Alamofire.request(URL_CHARGE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ChargeRes>) in
            completion(response)
        }
    }
    
    func updateDeviceToken(params: [String: Any], completion: @escaping ( _ response: DataResponse<NoDataRes>) -> Void) -> Void {
        Alamofire.request(URL_ACCOUNT_DEVICE_TOKEN_UPDATE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<NoDataRes>) in
            completion(response)
        }
    }
    
    func getAllRecentCall(completion: @escaping ( _ response: DataResponse<RecentCallRes>) -> Void) -> Void {
        Alamofire.request(URL_SCHEDULE_GET, method: .get, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<RecentCallRes>) in
            completion(response)
        }
    }
    
    func getAllSchedule(completion: @escaping ( _ response: DataResponse<ScheduleAllRes>) -> Void) -> Void {
        Alamofire.request(URL_SCHEDULE_GET, method: .get, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ScheduleAllRes>) in
            completion(response)
        }
    }
    
    func addSchedule(params: [String: Any], completion: @escaping ( _ response: DataResponse<ScheduleAddRes>) -> Void) -> Void {
        Alamofire.request(URL_SCHEDULE_ADD, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ScheduleAddRes>) in
            completion(response)
        }
    }
    
    func updateSchedule(params: [String: Any], completion: @escaping ( _ response: DataResponse<ScheduleUpdateRes>) -> Void) -> Void {
        Alamofire.request(URL_SCHEDULE_UPDATE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ScheduleUpdateRes>) in
            completion(response)
        }
    }
    
    func deleteSchedule(params: [String: Any], completion: @escaping ( _ response: DataResponse<NoDataRes>) -> Void) -> Void {
        Alamofire.request(URL_SCHEDULE_DELETE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<NoDataRes>) in
            completion(response)
        }
    }
    
    func getAllContact(completion: @escaping ( _ response: DataResponse<ContactAllRes>) -> Void) -> Void {
        Alamofire.request(URL_CONTACT_GET, method: .get, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<ContactAllRes>) in
            completion(response)
        }
    }
    
    func addContact(params: [String: Any], completion: @escaping ( _ response: DataResponse<NoDataRes>) -> Void) -> Void {
        Alamofire.request(URL_CONTACT_ADD, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<NoDataRes>) in
            completion(response)
        }
    }
    
    func updateContact(params: [String: Any], completion: @escaping ( _ response: DataResponse<NoDataRes>) -> Void) -> Void {
        Alamofire.request(URL_CONTACT_UPDATE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<NoDataRes>) in
            completion(response)
        }
    }
    
    func deleteContact(params: [String: Any], completion: @escaping ( _ response: DataResponse<NoDataRes>) -> Void) -> Void {
        Alamofire.request(URL_CONTACT_DELETE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<NoDataRes>) in
            completion(response)
        }
    }
    
    func getAllHelp(completion: @escaping ( _ response: DataResponse<HelpRes>) -> Void) -> Void {
        Alamofire.request(URL_HELP_GET, method: .get, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<HelpRes>) in
            completion(response)
        }
    }
    
    func updateAccount(params: [String: Any], completion: @escaping ( _ response: DataResponse<AccountUpdateRes>) -> Void) -> Void {
        Alamofire.request(URL_ACCOUNT_UPDATE, method: .post, parameters: params, encoding: JSONEncoding.default, headers: BEARER_HEADER()).responseObject { (response: DataResponse<AccountUpdateRes>) in
            completion(response)
        }
    }
}
