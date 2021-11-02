//
//  Const.swift
//  BlownUp-Cell-Swift
//
//  Created by Dove on 27/04/2021.
//

import Foundation
import Alamofire

//  ENV
let DEV_MODE = false
let PUBLISH_MODE = true
let LIVE_PAYMENT = true

//  API URLS
let DEV_SERVER = "http://192.168.109.72"
let PRODUCT_SERVER = PUBLISH_MODE ? "https://panel.blownup.co" : "http://dev-panel.blownup.co"
let BASE_SERVER = DEV_MODE ? DEV_SERVER : PRODUCT_SERVER

//  STRIPE PUBLISHABLE KEY
let STRIPE_PK_TEST = "pk_test_51IVQTuFmwQHroLNotyVUdfmRP83uYbtaecmidNUa1JdtnLUpySuEx5mzhF1E4fm46VG038uvsLBWBkaDYV72WZfV00vRbnMLv0"
let STRIPE_PK_LIVE = "pk_live_51IVQTuFmwQHroLNo5y9JhLuPnnbpMC2aG0PKGiNqAiuVjN5B4SCzURwetu4ZFNZzix6SV5XLTfp4O3THStK7OyGo002pHFXAxT"
let STRIPE_KEY = LIVE_PAYMENT ? STRIPE_PK_LIVE : STRIPE_PK_TEST

//  APPLE MERCHANT ID
let APPLE_MERCHANT_ID = "merchant.piexec.blownup.co"

//TERMS AND CONDITIONS
let APP_LANDING_URL = "https://blownup.co";
let TERMS_CONDITIONS_URL = "https://blownup.co/terms-and-conditions";
let PRIVACY_POLICY_URL = "https://blownup.co/privacy-policy";

//  STORE KEYS
let API_TOKEN = "API_TOKEN"
let USER_PROFILE = "USER_PROFILE"
let REMEMBER_ME = "REMEMBER_ME"
let CHARGED = "CHARGED"
let APPLE_USER_ID = "APPLE_USER_ID"
let APPLE_USER_EMAIL = "APPLE_USER_EMAIL"
let VOIP_TOKEN = "VOIP_TOKEN"

//  ViewControllers
let VC_LOGIN = "LoginVC"
let VC_SIGN_UP = "SignUpVC"
let VC_FORGET_PASSWORD = "ForgetPasswordVC"
let VC_VERIFY_CODE = "VerifyCodeVC"
let VC_RESET_PASSWORD = "ResetPasswordVC"
let VC_CARD_REGISTER = "CardRegisterVC"
let VC_SUCCESS = "SuccessVC"
let VC_RECENT_CALL = "RecentCallVC"
let VC_MAIN_TAB = "MainTabVC"
let VC_SCHEDULE_ADD = "ScheduleAddVC"
let VC_SCHEDULE_LIST = "ScheduleListVC"
let VC_CONTACT_ADD = "ContactAddVC"
let VC_CONTACT_LIST = "ContactListVC"
let VC_SETTING = "SettingVC"
let VC_HELP = "HelpVC"
let VC_ACCOUNT = "AccountVC"
let VC_DIALOG_CONTACT = "DialogContactVC"

//  API REQUEST HEADER
let HEADER = [ "Content-Type": "application/json"]
func BEARER_HEADER() -> HTTPHeaders {
    return [
        "Content-Type": "application/json",
        "Authorization": "Bearer \(Store.instance.apiToken)"]
}

//  Sign In/Up
let URL_LOGIN = "\(BASE_SERVER)/api/login"
let URL_SIGN_UP = "\(BASE_SERVER)/api/signup"

//  Forget Password
let URL_FORGET_PASSWORD = "\(BASE_SERVER)/api/password/forget"
let URL_RESET_PASSWORD = "\(BASE_SERVER)/api/password/reset"

//  Account
let URL_ACCOUNT_UPDATE = "\(BASE_SERVER)/api/account/update"
let URL_ACCOUNT_DEVICE_TOKEN_UPDATE = "\(BASE_SERVER)/api/account/update_device_token"

//  Checkout
let URL_CHARGE = "\(BASE_SERVER)/api/charge"
let URL_CHARGE_STATUS = "\(BASE_SERVER)/api/charge/status"

//  Schedule
let URL_SCHEDULE_GET = "\(BASE_SERVER)/api/schedule"
let URL_SCHEDULE_ADD = "\(BASE_SERVER)/api/schedule/add"
let URL_SCHEDULE_UPDATE = "\(BASE_SERVER)/api/schedule/update"
let URL_SCHEDULE_DELETE = "\(BASE_SERVER)/api/schedule/delete"

//  Contact
let URL_CONTACT_GET = "\(BASE_SERVER)/api/contact"
let URL_CONTACT_ADD = "\(BASE_SERVER)/api/contact/add"
let URL_CONTACT_UPDATE = "\(BASE_SERVER)/api/contact/update"
let URL_CONTACT_DELETE = "\(BASE_SERVER)/api/contact/delete"

//  Help
let URL_HELP_GET = "\(BASE_SERVER)/api/help"
