//
//  ThemeTextField.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  ThemeTextField.swift
//  InstaResolv
//
//  Created by Amarjith B on 27/02/26.
//

import SwiftUI

// ---------------------------------------------------------
// MARK: - ThemeTextField
// Usage:
//   ThemeTextField(title: "Full Name", placeholder: "Your full name", text: $name)
//   ThemeTextField(title: "Password",  placeholder: "Your password",  text: $pass, isSecure: true)
// ---------------------------------------------------------

struct ThemeTextField: View {
    
    // MARK: - Config
    let title: String
    let placeholder: String
    @Binding var text: String
    var isSecure: Bool = false
    var keyboardType: UIKeyboardType = .default
    
    // MARK: - State
    @State private var isRevealed: Bool = false
    
    // MARK: - Body
    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            titleLabel
            inputField
        }
    }
}

// MARK: - Subviews
private extension ThemeTextField {
    
    var titleLabel: some View {
        Text(title)
            .font(.semiBold(12))
            .foregroundStyle(Color.blackTheme)
    }
    
    var inputField: some View {
        HStack(spacing: 0) {
            fieldContent
                .padding(.leading, 14)
            if isSecure {
                eyeToggle
                    .padding(.trailing, 14)
            }
        }
        .frame(height: 50)
        .background(Color.placeHolder)
        .roundedCorners(10)
    }
    
    @ViewBuilder
    var fieldContent: some View {
        if isSecure {
            UIKitPasswordField(
                text: $text,
                placeholder: placeholder,
                isSecure: !isRevealed,
                font: .regular(14),
                textColor: UIColor(Color.blackTheme),
                placeholderColor: UIColor(Color.blackTheme.opacity(0.4)),
                isRTL: LocalizationService.shared.language.isRTLLanguage
            )
        } else {
            UIKitStyledTextField(
                text: $text,
                placeholder: placeholder,
                font: .regular(14),
                textColor: UIColor(Color.blackTheme),
                placeholderColor: UIColor(Color.blackTheme.opacity(0.4)),
                height: 50,
                isRTL: LocalizationService.shared.language.isRTLLanguage,
                keyboardType: keyboardType
            )
        }
    }
    
    var eyeToggle: some View {
        Button {
            isRevealed.toggle()
        } label: {
            Image(isRevealed ? .icEyeOpen : .icEyeClose)
                .renderingMode(.template)
                .foregroundStyle(Color.blackTheme.opacity(0.45))
        }
        .buttonStyle(.plain)
    }
}

