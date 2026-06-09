//
//  RightButtonView.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI

struct RightButtonView: View {
    var body: some View {
        ZStack {
            Circle()
                .fill(Color.primary)
                .frame(width: 52, height: 52)
            
            Image(.icWelcomeArrow)
                
        }
    }
}

#Preview {
    RightButtonView()
}
