//
//  LoginContentView.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI
import Shared


struct LoginContentView: View {
    // MARK: - State
    @State private var emailAddress: String = ""
    @State private var password: String = ""
    let viewModel = ViewModelFactory.shared.loginViewModel()
    
    // MARK: - Body
    var body: some View {
        NavigationStack {
            ZStack {
                Color.white.ignoresSafeArea()
                ScrollView(.vertical, showsIndicators: false) {
                    VStack(spacing: 0) {
                        Image(.icBannerLogo)
                            .resizable()
                            .scaledToFit()
                            .frame(width: 210, height: 62)
                            .padding(.top, 80)
                        
                        headingBlock
                            .padding(.top, 56)
                        
                        formBlock
                            .padding(.top, 32)
                        
                        loginButton
                            .padding(.top, 40)
                        
                        forgotPasswordButton
                            .padding(.top, 20)
                        
//                        if let versionString = Configurations.versionString {
//                            Text(versionString)
//                                .multilineTextAlignment(.center)
//                                .foregroundColor(Color.blackTheme.opacity(0.5))
//                                .font(.light(10))
//                                .padding(.top, 16)
//                                .padding(.bottom, 24)
//                        }
                    }
                    .padding(.horizontal, 24)
                }
            }
//            .toastOverlay(toast: $viewModel.toast, show: $viewModel.showToast)
            .navigationBarBackButtonHidden()
        }
    }
}

// MARK: - Subviews
private extension LoginContentView {

    var headingBlock: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("login.title".localizedString())
                .font(.bold(24))
                .foregroundStyle(Color.blackTheme)

//            HStack(spacing: 4) {
//                Text("login.not_member".localizedString())
//                    .font(.regular(14))
//                    .foregroundStyle(Color.blackTheme)
//                Button {
//                    viewControllerHolder?.present(style: .overCurrentContext) {
//                        RegisterContentView()
//                            .localize()
//                    }
//                } label: {
//                    Text("login.register_now".localizedString())
//                        .font(.regular(14))
//                        .foregroundStyle(Color.primary)
//                }
//                .buttonStyle(.plain)
//            }
        }
        .leftAligned()
    }

    var formBlock: some View {
        VStack(spacing: 20) {
            ThemeTextField(
                title: "field.title.email_id".localizedString(),
                placeholder: "field.placeholder.email".localizedString(),
                text: $emailAddress,
                keyboardType: .emailAddress
            )
            ThemeTextField(
                title: "field.title.password".localizedString(),
                placeholder: "field.placeholder.password".localizedString(),
                text: $password,
                isSecure: true
            )
        }
    }

    var loginButton: some View {
        ButtonWithLoader(
            action: { onSignIn() },
            title: "login.button".localizedString(),
            titleColor: .white,
            width: 327.ratiodWidth(),
            height: 48.ratiodHeight(),
            backgroundColor: Color.primary,
            font: .bold(16),
            isLoading: .constant(false)
        )
    }

    var forgotPasswordButton: some View {
        Button {
//            viewControllerHolder?.present(style: .overCurrentContext) {
//                ForgotPasswordView()
//                    .localize()
//            }
        } label: {
            Text("login.forgot_password".localizedString())
                .font(.regular(14))
                .foregroundStyle(Color.primary)
        }
        .buttonStyle(.plain)
    }
}

// MARK: - Actions
private extension LoginContentView {
    func onSignIn() {
        viewModel.updateEmail(value: emailAddress)
        viewModel.updatePassword(value: password)
        viewModel.login()
    }
}

#Preview {
    LoginContentView()
}
