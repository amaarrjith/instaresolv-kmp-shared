//
//  ThemeAlertView.swift
//  iosApp
//
//  Created by Amarjith B on 10/06/26.
//


import SwiftUI

struct ThemeAlertView: View {
    let title: String?
    let message: String
    var buttonTitle: String = "common.close".localizedString()
    var type: AlertType = .error
    var onOk: () -> Void
    
    var body: some View {
        ZStack {
            // Semi-transparent background overlay
            Color.black.opacity(0.4)
                .ignoresSafeArea()
                
            
            // Popup Container
            VStack(spacing: 0) {
                // Icon
                
                Image(type.icon)
                        .resizable()
                        .scaledToFit()
                        .frame(width: 80, height: 80)
                        .padding(.top, 40)
                
                
                // Title
                Text(title?.localizedString() ?? type.title)
                    .font(.bold(20))
                    .foregroundColor(.blackTheme)
                    .padding(.top, 24)
                    .padding(.horizontal, 24)
                    .multilineTextAlignment(.center)
                
                // Subtitle / Message
                Text(message.localizedString())
                    .font(.regular(14))
                    .foregroundColor(Color.alertTextSecondary)
                    .multilineTextAlignment(.center)
                    .lineSpacing(4)
                    .padding(.top, 16)
                    .padding(.horizontal, 32)
                
                // OK/Close Button
                Button(action: onOk) {
                    Text(buttonTitle.localizedString())
                        .font(.bold(16))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(type == .error ? Color.primary : Color.greenTheme)
                        .cornerRadius(26)
                }
                .padding(.top, 32)
                .padding(.horizontal, 24)
                .padding(.bottom, 24)
            }
            .frame(width: UIScreen.main.bounds.width - 48)
            .background(Color.white)
            .cornerRadius(30)
            .shadow(color: Color.black.opacity(0.15), radius: 25, x: 0, y: 10)
        }
        .transition(.opacity)
    }
}

#Preview {
    ThemeAlertView(
        title: "Alert Title",
        message: "This is a dynamic alert message to show how it looks with the same design as ExitPopup.",
        onOk: {}
    )
}


enum AlertType {
    case error
    case success
    
    var title: String {
        switch self {
        case .error: return "Error"
        case .success: return "Success"
        }
    }
    
    var icon: ImageResource {
        switch self {
        case .error:
                .icAlertWarning
        case .success:
                .icSuccessIcon
        }
    }
}
