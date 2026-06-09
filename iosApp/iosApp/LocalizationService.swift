//
//  LocalizationService.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI
import Combine

final class LocalizationService: ObservableObject {

    static let shared = LocalizationService()
    private let languageKey = "language"
    @Published private(set) var currentLanguage: Language

    private init() {
        if let savedValue = UserDefaults.standard.string(forKey: languageKey),
           let savedLanguage = Language(rawValue: savedValue) {
            currentLanguage = savedLanguage
        } else {
            currentLanguage = Language.devicePreferred
            UserDefaults.standard.setValue(currentLanguage.rawValue, forKey: languageKey)
        }
    }
    
    var language: Language {
        get {
            currentLanguage
        }
        set {
            setLanguage(newValue)
        }
    }
    
    func setLanguage(_ newLanguage: Language) {
        guard newLanguage != currentLanguage else { return }
        currentLanguage = newLanguage
        UserDefaults.standard.setValue(newLanguage.rawValue, forKey: languageKey)
    }
}
