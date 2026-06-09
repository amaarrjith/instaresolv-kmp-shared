//
//  WelcomeBanner.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI

// MARK: - Model

struct WelcomeBanner {
  let id: Int
  let title: String
  let description: String
}

// MARK: - Main View

struct WelcomeSliderView: View {

  @State private var currentIndex: Int = 0
  @State private var goToLogin: Bool = false

  private let bannerContent: [WelcomeBanner] = [
    WelcomeBanner(
      id: 1,
      title: "welcome.slide1.title".localizedString(),
      description: "welcome.slide1.description".localizedString()
    ),
    WelcomeBanner(
      id: 2,
      title: "welcome.slide2.title".localizedString(),
      description: "welcome.slide2.description".localizedString()
    ),
    WelcomeBanner(
      id: 3,
      title: "welcome.slide3.title".localizedString(),
      description: "welcome.slide3.description".localizedString()
    ),
  ]

  var body: some View {
    NavigationStack {
      ZStack {
        Color.white.ignoresSafeArea()
        contentView
      }
      .navigationBarBackButtonHidden()
      .navigationDestination(isPresented: $goToLogin) {
        LoginContentView()
      }
    }
  }

  // MARK: - Content View

  private var contentView: some View {
    ZStack(alignment: .bottom) {
      // Top grey area (phone image background)
      Color.gray
        .edgesIgnoringSafeArea(.top)

      // Phone mockup image
      Image(.icWelcomePhone)
        .resizable()
        .frame(width: 279, height: 557)
        .offset(y: -80)
        .zIndex(1)

      // Bottom white card with curved top
      VStack {
        Spacer()
        bottomCardView
      }
      .frame(height: screenHeight * 0.37)
      .zIndex(2)
    }
  }

  // MARK: - Bottom Card

  private var bottomCardView: some View {
    ZStack {
      TopCurveShape()
        .fill(Color.white)

        .ignoresSafeArea(edges: .bottom)

      VStack(spacing: 0) {
        sliderTabView
        navigationControlsView
      }
    }
  }

  // MARK: - Slider Tab View

  private var sliderTabView: some View {
    TabView(selection: $currentIndex) {
      ForEach(bannerContent.indices, id: \.self) { index in
        SlideContentView(
          banner: bannerContent[index],
          isFirstSlide: index == 0
        )
        .tag(index)
      }
    }
    .tabViewStyle(.page(indexDisplayMode: .never))

  }

  // MARK: - Dot Indicators + Navigation Buttons

  private var navigationControlsView: some View {
    HStack {
      backButton

      Spacer()

      dotsIndicator

      Spacer()

      nextButton
    }
    .padding(.horizontal, 25)
    .padding(.top, 25)
  }

  @ViewBuilder
  private var backButton: some View {
    if currentIndex > 0 {
      Button {
        withAnimation { currentIndex -= 1 }
      } label: {
        Image(.icArrowLeft)
          .resizable()
          .frame(width: 52, height: 52)
      }
    } else {
      Color.clear.frame(width: 52, height: 52)
    }
  }

  private var dotsIndicator: some View {
    HStack(spacing: 8) {
      ForEach(bannerContent.indices, id: \.self) { i in
        Circle()
          .fill(i <= currentIndex ? Color.primary : Color.grayDots)
          .frame(width: 10, height: 10)
          .animation(.easeInOut(duration: 0.2), value: currentIndex)
      }
    }
  }

  private var nextButton: some View {
    Button {
      withAnimation {
        if currentIndex < bannerContent.count - 1 {
          currentIndex += 1
        } else {
          UserDefaults.standard.set(true, forKey: "hasSeenWelcomeScreen")
          goToLogin = true
        }
      }
    } label: {
      RightButtonView()
    }
  }
}

// MARK: - Slide Content View

private struct SlideContentView: View {
  let banner: WelcomeBanner
  let isFirstSlide: Bool

  var body: some View {
    VStack(spacing: 0) {
      if isFirstSlide {
        firstSlideContent
      } else {
        otherSlideContent
      }
    }
  }

  // First slide: "Welcome to" + logo + description
  private var firstSlideContent: some View {
    VStack(spacing: 0) {
      Text(banner.title)
        .font(.regular(14))
        .foregroundStyle(.gray)
        .padding(.top, 49)
        .padding(.bottom, 10)

      Image(.icBannerLogo)
        .resizable()
        .scaledToFit()
        .frame(width: 210, height: 40)
        .padding(.bottom, 22)

      descriptionText
    }
  }

  // Other slides: bold title + description
  private var otherSlideContent: some View {
    VStack(spacing: 0) {
      Text(banner.title)
        .font(.extraBold(21))
        .multilineTextAlignment(.center)
        .foregroundStyle(Color.blueTheme)
        .padding(.top, 49)
        .padding(.bottom, 10)
        .padding(.horizontal, 32)

      descriptionText
    }
  }

  private var descriptionText: some View {
    Text(banner.description)
      .font(.regular(14))
      .foregroundStyle(.gray)
      .lineSpacing(4)
      .multilineTextAlignment(.center)
      .padding(.horizontal, 32)
  }
}

// MARK: - Top Curve Shape

extension WelcomeSliderView {
  struct TopCurveShape: Shape {
    func path(in rect: CGRect) -> Path {
      var path = Path()
      path.move(to: CGPoint(x: 0, y: rect.height))
      path.addLine(to: CGPoint(x: 0, y: 40))
      path.addQuadCurve(
        to: CGPoint(x: rect.width, y: 40),
        control: CGPoint(x: rect.width / 2, y: 65)
      )
      path.addLine(to: CGPoint(x: rect.width, y: rect.height))
      path.closeSubpath()
      return path
    }
  }
}

// MARK: - Preview

#Preview {
  WelcomeSliderView()
}
