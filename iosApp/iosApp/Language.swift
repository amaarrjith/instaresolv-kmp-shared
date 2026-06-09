//
//  Language.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  Language.swift
//  InstaResolv
//
//  Created by Amarjith B on 26/02/26.
//


import Foundation

enum Language: String, CaseIterable, Identifiable {
    var id: String { rawValue }
    
    case english = "en"
    case arabic = "ar"
    case urdu = "ur"
    case hindi = "hi"
    case spanish = "es"

    var repoValue: String {
        switch self {
        case .english:
            return "1"
        case .arabic:
            return "2"
        case .urdu:
            return "3"
        case .hindi:
            return "4"
        case .spanish:
            return "5"
        }
    }
    
    var description: String {
        switch self {
        case .english:
            return "English"
        case .arabic:
            return "العربية"
        case .urdu:
            return "اردو"
        case .hindi:
            return "हिन्दी"
        case .spanish:
            return "Español"
        }
    }
    
    var isRTLLanguage: Bool {
        switch self {
        case .english:
            false
        case .arabic:
            true
        case .urdu:
            true
        case .hindi:
            false
        case .spanish:
            false
        }
    }
    
    var local: Locale {
        switch self {
        case .english:
            return Locale(identifier: "en")
        case .arabic:
            return Locale(identifier: "ar")
        case .urdu:
            return Locale(identifier: "ur")
        case .hindi:
            return Locale(identifier: "hi")
        case .spanish:
            return Locale(identifier: "es")
        }
    }
    
    var shortCode: String {
        switch self {
        case .english:
            return "EN"
        case .arabic:
            return "AR"
        case .urdu:
            return "UR"
        case .hindi:
            return "HI"
        case .spanish:
            return "ES"
        }
        
    }

    var flag: String? {
        switch self {
        case .english:
            return "🇺🇸"
        case .arabic:
            return "🇸🇦"
        case .urdu:
            return "🇵🇰"
        case .hindi:
            return "🇮🇳"
        case .spanish:
            return "🇪🇸"
        }
    }
    
    var headerKey: String {
        switch self {
        case .english:
            return "en"
        case .arabic:
            return "ar"
        case .urdu:
            return "ur"
        case .hindi:
            return "hi"
        case .spanish:
            return "es"
        }
    }

    static var devicePreferred: Language {
        let preferred = Locale.preferredLanguages.first ?? "en"
        let languageCode = Locale(identifier: preferred).language.languageCode?.identifier ?? "en"
        return Language(rawValue: languageCode) ?? .english
    }
}
