//
//  Constants.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  Constants.swift
//  InstaResolv
//
//  Created by Amarjith B on 26/02/26.
//


import SwiftUI

struct Constants {
    struct UserDefaultKeys {
        static let LOGGED_IN_USER = "LOGGED_IN_USER"
        static let LOGGED_IN_USER_TOKEN = "LOGGED_IN_USER_TOKEN"
        static let LOGGED_IN_USER_REFRESH_TOKEN = "LOGGED_IN_USER_REFRESH_TOKEN"
    }
    
    struct Number {
        struct Limit {
            static let FULLNAME = 34
            static let PASSWORD = 20
            static let DESIGNATION = 35
            static let COMPANY = 35
            static let GROUP_NAME = 100
            static let DESCRIPTION = 500
            static let GROUPCODEPARTONE = 4
            static let GROUPCODEPARTTWO = 5
            
            struct Observation {
                static let TITLE = 250
                static let REPORTED_BY = 35
                static let LOCATION = 250
                static let DESCRIPTION = 1500
                static let IMAGE_DESCRIPTION = 250
            }
        }
        
        struct Duration {
            static let TOAST: Double = 8
            static let OTP_RESEND_TIME_SECONDS: Double = 300

        }
    }
    
    struct DateFormat {
        static let REPO_DATE = "dd-MM-yyyy"
        static let DATE = "yyyy-MM-dd"
        static let REPO_TIME = "HH:mm:ss"
        static let REPO_TIME_WITHOUT_SEC = "HH:mm"
        static let TIME_PLACEHOLDER = "HH:MM"
        static let REPO_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
    }
    
    struct NotificationKey {
        static let USER_INFO = "USER_INFO"
    }
    
    static let COMMON_ERROR_MESSAGE = "Something Went Wrong!!"
    static let PARSING_ERROR_MESSAGE = "\(COMMON_ERROR_MESSAGE) - Parsing failed"

    static let CURRENCY = "SAR"
    
    static let EMPTY_STRING = ""
    static let NA = "NA"
    static let SPACE = " "
    static let COLON = ":"
    static let ADD = "+"
    static let SLASH = "/"
    static let TAB = "\t"
    static let SINGLE_QUOTE = "'"
    static let PERIOD = "."
    static let DOUBLE_QUOTE = "\""
    static let COMMA = ","
    static let NEXT_LINE = "\n"
    static let PIPE = "|"
    static let HYPHEN = "-"
    static let TEXT_PLAIN = "text/plain"
    static let ENGLISH = "en"
    static let ARABIC = "ar"
    static let CONFIGURATIONS = "Configurations"
    static let BACKWARD_SLASHES = "\\"
    static let CF_BUNDLE_SHORT_VERSION_STRING = "CFBundleShortVersionString"
    static let NOTHING = "N/A"

}
