//
//  LeftAlignModifier.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import Foundation
import SwiftUI

// MARK: - Screen Dimensions
extension View {
    var screenHeight: CGFloat {
        UIScreen.main.bounds.height
    }
    
    var screenWidth: CGFloat {
        UIScreen.main.bounds.width
    }
}

// MARK: - Alignment
extension View {
    func leftAligned() -> some View {
        modifier(LeftAlignModifier())
    }
}

struct LeftAlignModifier: ViewModifier {
    func body(content: Content) -> some View {
        HStack {
            content
            Spacer()
        }
    }
}

// MARK: - Corner Radius
extension View {
    /// Clips the view to a rounded rectangle with the given radius.
    /// Usage: `.roundedCorners(12)` or `.roundedCorners(12, style: .continuous)`
    func roundedCorners(_ radius: CGFloat, style: RoundedCornerStyle = .continuous) -> some View {
        clipShape(RoundedRectangle(cornerRadius: radius, style: style))
    }
    
    /// Applies a rounded stroke border without clipping the view's content.
    /// Usage: `.roundedBorder(Color.primary, width: 1, cornerRadius: 8)`
    func roundedBorder(_ color: Color, width: CGFloat = 1, cornerRadius: CGFloat, style: RoundedCornerStyle = .continuous) -> some View {
        overlay(
            RoundedRectangle(cornerRadius: cornerRadius, style: style)
                .stroke(color, lineWidth: width)
        )
    }
}

extension View {
    func closeKeyboard() {
        DispatchQueue.main.async {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder),to: nil, from: nil, for: nil)
        }
    }
}


extension View {
    func leftAlign() -> some View {
        self.modifier(LeftAlignModifier())
    }
}


extension View {
    func localize() -> some View {
        self
            .environment(\.locale, .init(identifier: LocalizationService.shared.language.rawValue))
            .environment(\.layoutDirection, LocalizationService.shared.language.isRTLLanguage ? .rightToLeft : .leftToRight)
    }
}

extension View {
    func pullToRefresh(action: @escaping () -> Void) -> some View {
        self.refreshable {
            action()
        }
    }
}

extension View {
    func verticalCenter() -> some View {
        VStack {
            Spacer()
            self
            Spacer()
        }
    }
}
