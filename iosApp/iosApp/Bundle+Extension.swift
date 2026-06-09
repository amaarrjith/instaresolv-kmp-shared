//
//  Bundle+Extension.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import Foundation

extension Bundle {

    /// Safely resolves the language-specific .lproj bundle for localisation.
    /// Falls back to `Bundle.main` when the lproj folder is not yet registered
    /// as a known Xcode localization region (e.g. during early development).
    static func localizedBundle(for language: Language) -> Bundle {
        if let path = Bundle.main.path(forResource: language.rawValue, ofType: "lproj"),
           let bundle = Bundle(path: path) {
            return bundle
        }
        // Fallback: Xcode hasn't registered the region yet — use main bundle
        // (NSLocalizedString will still return the key as-is, which is better than a crash)
        return Bundle.main
    }
}