//
//  AppToast.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI

// ---------------------------------------------------------
// MARK: - Toast Model
// ---------------------------------------------------------

struct AppToast: Equatable {
    enum Style {
        case success   // green accent
        case warning   // orange accent
        case error     // red accent  ← matches the screenshot
        case info      // blue accent
    }

    var title: String        // bold label ("Error", "Success" …)
    var message: String      // body text
    var style: Style
    var duration: Double     // seconds before auto-dismiss (0 = manual only)

    // MARK: - Convenience factories
    static func error(title: String = "Error", _ message: String, duration: Double = 4) -> AppToast {
        AppToast(title: title, message: message, style: .error, duration: duration)
    }
    static func success(title: String = "Success", _ message: String, duration: Double = 3) -> AppToast {
        AppToast(title: title, message: message, style: .success, duration: duration)
    }
    static func warning(title: String = "Warning", _ message: String, duration: Double = 4) -> AppToast {
        AppToast(title: title, message: message, style: .warning, duration: duration)
    }
    static func info(title: String = "Info", _ message: String, duration: Double = 3) -> AppToast {
        AppToast(title: title, message: message, style: .info, duration: duration)
    }
}

// ---------------------------------------------------------
// MARK: - Style Tokens
// ---------------------------------------------------------

private extension AppToast.Style {
    var accentColor: Color {
        switch self {
        case .success: return Color.toastSuccess
        case .warning: return Color.toastWarning
        case .error:   return Color.errorRed          // app red #D42027
        case .info:    return Color.toastInfo
        }
    }
    var bgColor: Color {
        switch self {
        case .success: return Color.toastSuccessBackground
        case .warning: return Color.toastWarningBackground
        case .error:   return Color.toastErrorBackground
        case .info:    return Color.toastInfoBackground
        }
    }
    var iconName: String {
        switch self {
        case .success: return "checkmark.circle"
        case .warning: return "exclamationmark.triangle"
        case .error:   return "exclamationmark.circle"
        case .info:    return "info.circle"
        }
    }
}

// ---------------------------------------------------------
// MARK: - ToastView
// ---------------------------------------------------------

struct ToastView: View {
    let toast: AppToast
    var onDismiss: () -> Void

    var body: some View {
        HStack(alignment: .center, spacing: 12) {
            // Left icon
            Image(systemName: toast.style.iconName)
                .font(.system(size: 20, weight: .semibold))
                .foregroundStyle(toast.style.accentColor)
                .frame(width: 24, height: 24)

            // Text block
            VStack(alignment: .leading, spacing: 2) {
                if !toast.title.isEmpty {
                    Text(toast.title)
                        .font(.semiBold(13))
                        .foregroundStyle(toast.style.accentColor)
                }
                Text(toast.message)
                    .font(.regular(12))
                    .foregroundStyle(Color.blackTheme.opacity(0.75))
                    .fixedSize(horizontal: false, vertical: true)
            }

            Spacer(minLength: 4)

            // Dismiss button
            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundStyle(Color.blackTheme.opacity(0.4))
            }
            .buttonStyle(.plain)
        }
        .padding(.vertical, 14)
        .padding(.horizontal, 16)
        .background(toast.style.bgColor)
        .overlay(
            // Left accent bar — matches design
            Rectangle()
                .fill(toast.style.accentColor)
                .frame(width: 4),
            alignment: .leading
        )
        .roundedCorners(10)
        .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 4)
        .padding(.horizontal, 16)
    }
}

// ---------------------------------------------------------
// MARK: - Toast Container Modifier
// Overlay this on your root ZStack / ContentView.
//
// Usage:
//   .toastOverlay(toast: $viewModel.toast, show: $viewModel.showToast)
// ---------------------------------------------------------

struct ToastContainerModifier: ViewModifier {
    @Binding var toast: AppToast?
    @Binding var show: Bool

    func body(content: Content) -> some View {
        ZStack(alignment: .bottom) {
            content
            if show, let t = toast {
                ToastView(toast: t) {
                    dismiss()
                }
                .transition(
                    .asymmetric(
                        insertion: .move(edge: .bottom).combined(with: .opacity),
                        removal:   .move(edge: .bottom).combined(with: .opacity)
                    )
                )
                .zIndex(999)
                .padding(.bottom, 40) // Bottom stack needs bottom padding to clear the home bar
                .task(id: t.title + t.message) {
                    guard t.duration > 0 else { return }
                    try? await Task.sleep(nanoseconds: UInt64(t.duration * 1_000_000_000))
                    guard !Task.isCancelled else { return } // Don't hide if replaced by a new toast
                    dismiss()
                }
            }
        }
        .animation(.spring(response: 0.35, dampingFraction: 0.8), value: show)
    }

    private func dismiss() {
        // Animate out
        withAnimation(.easeOut(duration: 0.25)) { show = false }
        
        // Wait for removal animation to complete, then clear the toast so it resets completely
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.3) {
            if !show { toast = nil }
        }
    }
}

extension View {
    /// Overlays an animated toast banner at the top of any view.
    /// - Parameters:
    ///   - toast: Binding to the current `AppToast?`. Set this to trigger.
    ///   - show:  Binding to the visibility flag (`showToast` on BaseViewModel).
    func toastOverlay(toast: Binding<AppToast?>, show: Binding<Bool>) -> some View {
        modifier(ToastContainerModifier(toast: toast, show: show))
    }
}

// ---------------------------------------------------------
// MARK: - Preview
// ---------------------------------------------------------

#Preview {
    VStack(spacing: 20) {
        ToastView(toast: .error("No internet connection. Connect to the internet to sync your data.")) {}
        ToastView(toast: .success("Profile updated successfully.")) {}
        ToastView(toast: .warning("Your session is about to expire.")) {}
        ToastView(toast: .info("New updates are available.")) {}
    }
    .padding(.vertical, 40)
}
