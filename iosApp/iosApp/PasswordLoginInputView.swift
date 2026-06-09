//
//  PasswordLoginInputView.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  PasswordLoginInputView.swift
//  InstaResolv
//
//  Created by Amarjith B on 27/02/26.
//


//
//  LogInInputViews.swift
//  ALNASR
//

import SwiftUI
import UIKit

// ---------------------------------------------------------
// MARK: - PASSWORD LOGIN INPUT VIEW
// ---------------------------------------------------------

struct PasswordLoginInputView: View {
    @Binding var password: String
    @State var isPasswordShow: Bool?

    var body: some View {
        HStack {
            UIKitPasswordField(
                text: $password,
                placeholder: "field.placeholder.password".localizedString(),
                isSecure: isPasswordShow ?? false ? false : true,                   // normal mode
                font: .regular(12),
                textColor: UIColor(Color.blackTheme),
                placeholderColor: UIColor(Color.blackTheme.opacity(0.5)),
                isRTL: LocalizationService.shared.language.isRTLLanguage
            )
            .padding(.leading, 10.5)

            Spacer()
            
            Button {
                isPasswordShow = !(isPasswordShow ?? false)
            } label: {
                Image(isPasswordShow ?? false ? .icEyeOpen : .icEyeClose)
            }
            .padding(.trailing, 15)
        }
    }
}

// ---------------------------------------------------------
// MARK: - PASSWORD (CONFIRM) VIEW
// ---------------------------------------------------------

struct PasswordView: View {

    @Binding var password: String
    var isConfirmPassword: Bool = false

    var body: some View {
        VStack {
            
                Text(isConfirmPassword ? "confirm_password" : "password")
                    .font(.regular(12))
                    .foregroundColor(Color.blackTheme)
                    .leftAligned()
            

            
                UIKitPasswordField(
                    text: $password,
                    placeholder: "field.placeholder.password".localizedString(),
                    isSecure: true,
                    font: .regular(12),
                    textColor: UIColor(Color.blackTheme),
                    placeholderColor: UIColor(Color.blackTheme.opacity(0.5)),
                    isRTL: LocalizationService.shared.language.isRTLLanguage
                )
                .leftAligned()
            

            if !isConfirmPassword {
                Divider()
                    .frame(height: 1)
            }
        }
    }
}

// ---------------------------------------------------------
// MARK: - EMAIL INPUT VIEW
// ---------------------------------------------------------

struct EmailInputView: View {
    @Binding var email: String

    var body: some View {
        HStack {

            UIKitStyledTextField(
                text: $email,
                placeholder: "field.placeholder.email".localizedString(),
                font: .regular(12),
                textColor: UIColor(Color.blackTheme),
                placeholderColor: UIColor(Color.blackTheme.opacity(0.5)),
                height: 41,
                isRTL: LocalizationService.shared.language.isRTLLanguage,
                keyboardType: .asciiCapable
            )
            .padding(.leading, 10.5)
        }
    }
}

// ---------------------------------------------------------
// MARK: - LOGIN INPUT VIEW MODIFIER
// ---------------------------------------------------------

struct LoginInputViewModifier: ViewModifier {

    var frame: CGRect
    var backgroundColor = Color.placeHolder
    var title: String

    func body(content: Content) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title)
                .font(.semiBold(12))
                .foregroundStyle(Color.blackTheme)
            content
                .frame(width: frame.width, height: 44)
                .background(backgroundColor)
                .cornerRadius(7)
        }
    }
}

// ---------------------------------------------------------
// MARK: - UIKit NORMAL TEXTFIELD WRAPPER
// ---------------------------------------------------------

struct UIKitStyledTextField: UIViewRepresentable {
    @Binding var text: String

    var placeholder: String
    var font: UIFont
    var textColor: UIColor
    var placeholderColor: UIColor
    var height: CGFloat
    var isRTL: Bool
    var keyboardType: UIKeyboardType

    func makeCoordinator() -> Coordinator { Coordinator(self) }

    func makeUIView(context: Context) -> StyledTextFieldUIKit {
        let view = StyledTextFieldUIKit()
        view.delegate = context.coordinator
        return view
    }

    func updateUIView(_ uiView: StyledTextFieldUIKit, context: Context) {
        uiView.configure(
            text: text,
            placeholder: placeholder,
            font: font,
            textColor: textColor,
            placeholderColor: placeholderColor,
            height: height,
            isRTL: isRTL,
            keyboardType: keyboardType,
        )
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: UIKitStyledTextField
        
        init(_ parent: UIKitStyledTextField) { self.parent = parent }

        func textFieldDidChangeSelection(_ textField: UITextField) {
            parent.text = textField.text ?? ""
        }
    }
}

// ---------------------------------------------------------
// MARK: - UIKit SECURE / NORMAL PASSWORD FIELD WRAPPER
// ---------------------------------------------------------

struct UIKitPasswordField: UIViewRepresentable {
    @Binding var text: String
    var placeholder: String
    var isSecure: Bool
    var font: UIFont
    var textColor: UIColor
    var placeholderColor: UIColor
    var isRTL: Bool
    var isPlaceholderShown: Bool = true
    func makeCoordinator() -> Coordinator { Coordinator(self) }

    func makeUIView(context: Context) -> PasswordTextFieldUIKit {
        let field = PasswordTextFieldUIKit()
        field.delegate = context.coordinator
        return field
    }

    func updateUIView(_ uiView: PasswordTextFieldUIKit, context: Context) {
        uiView.configure(
            text: text,
            placeholder: placeholder,
            isSecure: isSecure,
            font: font,
            textColor: textColor,
            placeholderColor: placeholderColor,
            isRTL: isRTL,
            isPlaceHolderShown: isPlaceholderShown
        )
    }

    class Coordinator: NSObject, UITextFieldDelegate {
        var parent: UIKitPasswordField

        init(_ parent: UIKitPasswordField) { self.parent = parent }

        func textFieldDidChangeSelection(_ textField: UITextField) {
            parent.text = textField.text ?? ""
        }
    }
}

// ---------------------------------------------------------
// MARK: - UIKit NORMAL TEXTFIELD
// ---------------------------------------------------------

class StyledTextFieldUIKit: UIView, UITextFieldDelegate {

    let textField = UITextField()
    weak var delegate: UITextFieldDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame); setupUI()
    }
    required init?(coder: NSCoder) { super.init(coder: coder); setupUI() }

    private func setupUI() {
        addSubview(textField)

        textField.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            textField.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 0),
            textField.trailingAnchor.constraint(equalTo: trailingAnchor),
            textField.topAnchor.constraint(equalTo: topAnchor),
            textField.bottomAnchor.constraint(equalTo: bottomAnchor),
            heightAnchor.constraint(equalToConstant: 41)
        ])
    }

    func configure(
        text: String,
        placeholder: String,
        font: UIFont,
        textColor: UIColor,
        placeholderColor: UIColor,
        height: CGFloat,
        isRTL: Bool,
        keyboardType: UIKeyboardType
    ) {
        textField.text = text
        textField.autocapitalizationType = .none
        textField.font = font
        textField.textColor = textColor
        textField.delegate = delegate
        textField.keyboardType = keyboardType
        textField.attributedPlaceholder = NSAttributedString(
            string: placeholder,
            attributes: [.foregroundColor: placeholderColor, .font: font]
        )

        textField.textAlignment = isRTL ? .right : .left
        textField.semanticContentAttribute = isRTL ? .forceRightToLeft : .forceLeftToRight
        frame.size.height = height
    }
}

// ---------------------------------------------------------
// MARK: - UIKit SECURE TEXTFIELD
// ---------------------------------------------------------

class PasswordTextFieldUIKit: UIView, UITextFieldDelegate {

    let textField = UITextField()
    weak var delegate: UITextFieldDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame); setupUI()
    }
    required init?(coder: NSCoder) { super.init(coder: coder); setupUI() }

    private func setupUI() {
        addSubview(textField)

        textField.translatesAutoresizingMaskIntoConstraints = false

        NSLayoutConstraint.activate([
            textField.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 0),
            textField.trailingAnchor.constraint(equalTo: trailingAnchor),
            textField.topAnchor.constraint(equalTo: topAnchor),
            textField.bottomAnchor.constraint(equalTo: bottomAnchor),
            heightAnchor.constraint(equalToConstant: 41)
        ])
    }

    func configure(
        text: String,
        placeholder: String,
        isSecure: Bool,
        font: UIFont,
        textColor: UIColor,
        placeholderColor: UIColor,
        isRTL: Bool,
        isPlaceHolderShown: Bool
    ) {
        textField.text = text
        textField.font = font
        textField.textColor = textColor
        textField.isSecureTextEntry = isSecure
        textField.delegate = delegate
        if isPlaceHolderShown {
            textField.attributedPlaceholder = NSAttributedString(
                string: placeholder,
                attributes: [.foregroundColor: placeholderColor, .font: font]
            )
        }
        textField.textAlignment = isRTL ? .right : .left
        textField.semanticContentAttribute = isRTL ? .forceRightToLeft : .forceLeftToRight
    }
}
