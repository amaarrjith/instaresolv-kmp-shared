//
//  GradientLoadingBar.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import SwiftUI

struct GradientLoadingBar: View {
    var progress: CGFloat // 40% (like your image)
    
    var body: some View {
        GeometryReader { geo in
            let width = geo.size.width * progress
            
            ZStack(alignment: .leading) {
                // Background track
                Capsule()
                    .fill(Color.white.opacity(0.4))
                    .shadow(color: .gray.opacity(0.2), radius: 2, x: 0, y: 1)
                
                // Progress bar
                Capsule()
                    .fill(Color.white)
                    .shadow(color: .gray.opacity(0.2), radius: 2, x: 0, y: 1)
                    .frame(width: width)
            }
        }
        .frame(width: 130)
        .frame(height: 6)
        .padding(.horizontal)
        .animation(.easeInOut(duration: 0.5), value: progress)
    }
}

struct GradientLoadingBar_Previews: PreviewProvider {
    static var previews: some View {
        GradientLoadingBar(progress: 1.0)
            .frame(width: 300, height: 20)
    }
}