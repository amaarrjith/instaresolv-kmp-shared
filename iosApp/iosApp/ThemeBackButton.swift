//
//  ThemeBackButton.swift
//  iosApp
//
//  Created by Amarjith B on 10/06/26.
//


import SwiftUI

// ---------------------------------------------------------
// MARK: - ThemeBackButton
// A reusable standalone back button.
// Usage:
//   ThemeBackButton { dismiss() }
//   ThemeBackButton(tint: .white) { dismiss() }
// ---------------------------------------------------------

struct ThemeBackButton: View {

    var tint: Color = Color.blackTheme
    var action: () -> Void

    var body: some View {
        Button(action: action) {
            Image(systemName: "chevron.left")
                .font(.system(size: 16, weight: .semibold))
                .foregroundStyle(tint)
                .frame(width: 36, height: 36)
                .background(Color.placeHolder)
                .roundedCorners(10)
                .rotationEffect(.degrees(LocalizationService.shared.currentLanguage.isRTLLanguage ? 180 : 0))
        }
        .buttonStyle(.plain)
    }
}

// ---------------------------------------------------------
// MARK: - View Modifier  →  .themeNavigationBar(...)
// Adds a top bar with a back chevron and an optional title.
// Usage:
//   MyView()
//       .themeNavigationBar("Register") { dismiss() }
//       .themeNavigationBar { dismiss() }          // no title
// ---------------------------------------------------------

struct ThemeNavigationBarModifier: ViewModifier {

    var title: String
    var tint: Color
    var onBack: () -> Void

    func body(content: Content) -> some View {
        VStack(spacing: 0) {
            // Navigation bar row
            HStack(spacing: 12) {
                ThemeBackButton(tint: tint, action: onBack)
                if !title.isEmpty {
                    Text(title)
                        .font(.semiBold(17))
                        .foregroundStyle(tint == .white ? Color.white : Color.blackTheme)
                }
                Spacer()
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 12)

            content
        }
    }
}

extension View {
    /// Adds the common top navigation bar with chevron back button.
    /// - Parameters:
    ///   - title: Optional title shown next to the back button. Defaults to empty (no title).
    ///   - tint: Icon & title colour. Defaults to `Color.blackTheme`.
    ///   - onBack: Closure called when the back button (or any equivalent) is tapped.
    func themeNavigationBar(
        _ title: String = "",
        tint: Color = Color.blackTheme,
        onBack: @escaping () -> Void
    ) -> some View {
        modifier(ThemeNavigationBarModifier(title: title, tint: tint, onBack: onBack))
    }
}

// MARK: - Preview
#Preview {
    VStack {
        Color.white
    }
    .themeNavigationBar("Register") {}
}
