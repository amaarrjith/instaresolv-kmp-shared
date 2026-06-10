//
//  ForgotPasswordView.swift
//  iosApp
//
//  Created by Amarjith B on 10/06/26.
//


import SwiftUI

struct ForgotPasswordView: View {
    // MARK: - State
    @Environment(\.dismiss) private var dismiss
    @State private var emailId: String = Constants.EMPTY_STRING
    @State private var isLoading: Bool = false
    @State private var popUpContentMessage: String? = Constants.EMPTY_STRING
    @State private var isPopUpShowing: Bool = false

    // MARK: - Dismiss
    private func goBack() {
        dismiss()
    }

    // MARK: - Body
    var body: some View {
        ZStack {
            Color.white.ignoresSafeArea()
            
            VStack(spacing: 0) {
                HStack {
                    ThemeBackButton(action: goBack)
                    Spacer()
                }
                ScrollView(showsIndicators: false) {
                    VStack(spacing: 0) {
                        iconBlock
                        
                        // Title
                        headingTitle
                        
                        // Subtitle
                        headingSubtitle
                        
                        // Email Field
                        formBlock
                        
                        // Button
                        resetButton

                        Spacer().frame(height: 60)
                    }
                }
            }
            .padding(.horizontal, 24)
            
            if self.isPopUpShowing {
                ThemeAlertView(title: nil, message: popUpContentMessage ?? Constants.EMPTY_STRING, type: .success) {
                    isPopUpShowing = false
                }
            }
           
        }
//        .toastOverlay(toast: $viewModel.toast, show: $viewModel.showToast)
    }
}

// MARK: - Subviews
private extension ForgotPasswordView {

    var iconBlock: some View {
        Image(.icForgotPassword)
            .resizable()
            .scaledToFit()
            .frame(width: 88.8, height: 86.17)
            .padding(.top, 78)
    }

    var headingTitle: some View {
        Text("forgot_password.title".localizedString())
            .font(.bold(18))
            .multilineTextAlignment(.center)
            .foregroundStyle(Color.blackTheme)
            .padding(.top, 60) // Offset from icon
    }

    var headingSubtitle: some View {
        Text("forgot_password.subtitle".localizedString())
            .font(.regular(14))
            .multilineTextAlignment(.center)
            .foregroundStyle(Color.blackTheme.opacity(0.6))
            .padding(.top, 20) // Offset from title
    }

    var formBlock: some View {
        ThemeTextField(
            title: "field.title.email_id".localizedString(),
            placeholder: "field.placeholder.email".localizedString(),
            text: $emailId,
            keyboardType: .emailAddress
        )
        .padding(.top, 62) // Offset from subtitle
    }

    var resetButton: some View {
        ButtonWithLoader(
            action: submit,
            title: "forgot_password.button".localizedString(),
            titleColor: .white,
            width: 327,
            height: 48,
            backgroundColor: Color.primary,
            font: .bold(16),
            isLoading: .constant(false)
        )
        .padding(.top, 62) // Offset from formBlock
    }
    
    func submit() {
        closeKeyboard()
//        viewModel.resetPassword(email: emailId) { completed,message  in
//            if completed {
//                self.popUpContentMessage = message
//                self.isPopUpShowing = true
//            }
//        }
    }
}

#Preview {
    ForgotPasswordView()
}
