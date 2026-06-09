//
//  SplashView.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI

struct SplashView: View {
    @State private var progress: CGFloat = 0
    @State private var isLoadingCompleted: Bool = false
    @State private var showTabBar: Bool = false
//    @StateObject var viewModel: SplashViewModel = SplashViewModel()
    @AppStorage("hasSeenWelcomeScreen") private var hasSeenWelcomeScreen: Bool = false
//    var isGuest: Bool? {
//        UserManager.getCheckOutUser()?.isGuestUser
//    }
    
    var body: some View {
        Group {
            if isLoadingCompleted {
                if hasSeenWelcomeScreen {
                    LoginContentView()
                        .localize()
                } else {
                    WelcomeSliderView()
                        .localize()
                }
            } else {
                ZStack {
                    Image(.icSplashBackground)
                        .resizable()
                        .scaledToFill()
                        .ignoresSafeArea()
                    
                    splashContent
                }
            }
        }
        .onAppear {
            timerStart()
            userCheckout()
        }
        .fullScreenCover(isPresented: $showTabBar) {
//            AppTabBarView()
//                .localize()
            
        }
    }
    
    var splashContent: some View {
        VStack(spacing: 10) {
            Image(.icAppLogo)
                .resizable()
                .frame(width:96,height: 96)
//            if let error = viewModel.error {
//                errorView(error: error)
//            } else {
                GradientLoadingBar(progress: progress)
//            }
        }
    }
    
//    private func errorView(error: SystemError) -> some View {
//        VStack(spacing: 24) {
//            Text(error.localizedDescription)
//                .font(.regular(13))
//                .foregroundStyle(Color.white)
//                .multilineTextAlignment(.center)
//
//            Button {
//                progress = 0
//                timerStart()
//                userCheckout()
//            } label: {
//                Text("try_again".localizedString())
//                    .font(.semiBold(13))
//                    .padding(.horizontal, 16)
//                    .padding(.vertical, 8)
//                    .foregroundColor(.white)
//                    .background(
//                        RoundedRectangle(cornerRadius: 20, style: .continuous)
//                            .stroke(Color.white, lineWidth: 1)
//                    )
//            }
//        }
//        .padding()
//    }
    
    private func timerStart() {
        Timer.scheduledTimer(withTimeInterval: 2.0, repeats: true) { timer in
            withAnimation(.easeInOut(duration: 0.5)) {
                if progress < 0.5 {
                    progress += 0.5
                } else {
                    timer.invalidate()
                }
            }
        }
    }
    
//    private func handleFlow() {
//        Log.print("SplashView: handleFlow called, isGuest: \(String(describing: isGuest))")
//        if isGuest ?? true {
//            Log.print("SplashView: Navigating to Login/Welcome (isLoadingCompleted = true)")
//            isLoadingCompleted = true
//        } else {
//            Log.print("SplashView: Presenting AppTabBarView")
//            showTabBar = true
//        }
//    }
    
    private func userCheckout() {
//        viewModel.userCheckout { completed in
//            if completed {
//                Log.print("SplashView: Initial userCheckout completed: \(completed)")
//                progress = 1.0
//                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
//                    handleFlow()
//                }
//            }
//        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            isLoadingCompleted = true
        }
    }
}

#Preview {
    SplashView()
}
