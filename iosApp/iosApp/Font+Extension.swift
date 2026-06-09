//
//  InterWeight.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  FontExtension.swift
//  InstaResolv
//
//  Created by Amarjith B on 26/02/26.
//

import SwiftUI

extension Font {
    
    // MARK: - Inter Font Weights
    // NOTE: PostScript names use "Inter18pt-" prefix (verified from actual TTF metadata)
    enum InterWeight: String {
        case thin               = "Inter18pt-Thin"
        case thinItalic         = "Inter18pt-ThinItalic"
        case extraLight         = "Inter18pt-ExtraLight"
        case extraLightItalic   = "Inter18pt-ExtraLightItalic"
        case light              = "Inter18pt-Light"
        case lightItalic        = "Inter18pt-LightItalic"
        case regular            = "Inter18pt-Regular"
        case italic             = "Inter18pt-Italic"
        case medium             = "Inter18pt-Medium"
        case mediumItalic       = "Inter18pt-MediumItalic"
        case semiBold           = "Inter18pt-SemiBold"
        case semiBoldItalic     = "Inter18pt-SemiBoldItalic"
        case bold               = "Inter18pt-Bold"
        case boldItalic         = "Inter18pt-BoldItalic"
        case extraBold          = "Inter18pt-ExtraBold"
        case extraBoldItalic    = "Inter18pt-ExtraBoldItalic"
        case black              = "Inter18pt-Black"
        case blackItalic        = "Inter18pt-BlackItalic"
    }
    
    static func inter(_ weight: InterWeight, size: CGFloat) -> Font {
        return .custom(weight.rawValue, size: size)
    }
}

// MARK: - Convenience Shorthands
extension Font {
    static func thin(_ size: CGFloat) -> Font          { .inter(.thin, size: size) }
    static func extraLight(_ size: CGFloat) -> Font    { .inter(.extraLight, size: size) }
    static func light(_ size: CGFloat) -> Font         { .inter(.light, size: size) }
    static func regular(_ size: CGFloat) -> Font       { .inter(.regular, size: size) }
    static func medium(_ size: CGFloat) -> Font        { .inter(.medium, size: size) }
    static func semiBold(_ size: CGFloat) -> Font      { .inter(.semiBold, size: size) }
    static func bold(_ size: CGFloat) -> Font          { .inter(.bold, size: size) }
    static func extraBold(_ size: CGFloat) -> Font     { .inter(.extraBold, size: size) }
    static func black(_ size: CGFloat) -> Font         { .inter(.black, size: size) }
}



extension UIFont {
    
    // MARK: - Inter Font Weights
    // NOTE: PostScript names use "Inter18pt-" prefix (verified from actual TTF metadata)
    enum InterWeight: String {
        case thin               = "Inter18pt-Thin"
        case thinItalic         = "Inter18pt-ThinItalic"
        case extraLight         = "Inter18pt-ExtraLight"
        case extraLightItalic   = "Inter18pt-ExtraLightItalic"
        case light              = "Inter18pt-Light"
        case lightItalic        = "Inter18pt-LightItalic"
        case regular            = "Inter18pt-Regular"
        case italic             = "Inter18pt-Italic"
        case medium             = "Inter18pt-Medium"
        case mediumItalic       = "Inter18pt-MediumItalic"
        case semiBold           = "Inter18pt-SemiBold"
        case semiBoldItalic     = "Inter18pt-SemiBoldItalic"
        case bold               = "Inter18pt-Bold"
        case boldItalic         = "Inter18pt-BoldItalic"
        case extraBold          = "Inter18pt-ExtraBold"
        case extraBoldItalic    = "Inter18pt-ExtraBoldItalic"
        case black              = "Inter18pt-Black"
        case blackItalic        = "Inter18pt-BlackItalic"
    }
    
    static func inter(_ weight: InterWeight, size: CGFloat) -> UIFont {
        return UIFont(name: weight.rawValue, size: size)
            ?? UIFont.systemFont(ofSize: size)
    }
}

// MARK: - Convenience Shorthands
extension UIFont {
    static func thin(_ size: CGFloat) -> UIFont          { .inter(.thin, size: size) }
    static func extraLight(_ size: CGFloat) -> UIFont    { .inter(.extraLight, size: size) }
    static func light(_ size: CGFloat) -> UIFont         { .inter(.light, size: size) }
    static func regular(_ size: CGFloat) -> UIFont       { .inter(.regular, size: size) }
    static func medium(_ size: CGFloat) -> UIFont        { .inter(.medium, size: size) }
    static func semiBold(_ size: CGFloat) -> UIFont      { .inter(.semiBold, size: size) }
    static func bold(_ size: CGFloat) -> UIFont          { .inter(.bold, size: size) }
    static func extraBold(_ size: CGFloat) -> UIFont     { .inter(.extraBold, size: size) }
    static func black(_ size: CGFloat) -> UIFont         { .inter(.black, size: size) }
}
