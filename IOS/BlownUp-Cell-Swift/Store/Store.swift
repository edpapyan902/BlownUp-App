//
//  Store.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation

class Store {
    
    static let instance = Store()
    
    let defaults = UserDefaults.standard

    var apiToken: String {
        get {
            return defaults.value(forKey: API_TOKEN) as? String ?? ""
        }
        set {
            defaults.set(newValue, forKey: API_TOKEN)
        }
    }
    
    var voipToken: String {
        get {
            return defaults.value(forKey: VOIP_TOKEN) as? String ?? ""
        }
        set {
            defaults.set(newValue, forKey: VOIP_TOKEN)
        }
    }
    
    var user: User? {
        get {
            if let data = defaults.data(forKey: USER_PROFILE) {
                do {
                    let decoder = JSONDecoder()
                    let user = try decoder.decode(User.self, from: data)
                    return user
                } catch {
                    print("Unable to Decode User Profile (\(error))")
                    return nil
                }
            }
            return nil
        }
        set {
            do {
                let encoder = JSONEncoder()
                let data = try encoder.encode(newValue)
                defaults.set(data, forKey: USER_PROFILE)
            } catch {
                print("Unable to Encode User Profile (\(error))")
            }
        }
    }
    
    func removeObject(key: String) {
        defaults.removeObject(forKey: key)
    }
    
    var rememberMe: Bool {
        get {
            return defaults.value(forKey: REMEMBER_ME) as? Bool ?? false
        }
        set {
            defaults.set(newValue, forKey: REMEMBER_ME)
        }
    }
    
    var charged: Bool {
        get {
            return defaults.value(forKey: CHARGED) as? Bool ?? false
        }
        set {
            defaults.set(newValue, forKey: CHARGED)
        }
    }
    
    var appleUserId: String {
        get {
            return defaults.value(forKey: APPLE_USER_ID) as? String ?? ""
        }
        set {
            defaults.set(newValue, forKey: APPLE_USER_ID)
        }
    }
    
    var appleUserEmail: String {
        get {
            return defaults.value(forKey: APPLE_USER_EMAIL) as? String ?? ""
        }
        set {
            defaults.set(newValue, forKey: APPLE_USER_EMAIL)
        }
    }
}
