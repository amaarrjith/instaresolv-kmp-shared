//
//  ButtonWithLoader.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  ButtonWithLoader.swift
//  InstaResolv
//
//  Created by Amarjith B on 27/02/26.
//


import SwiftUI

struct ButtonWithLoader: View {
    
    var title: String
    var titleColor: Color
    var action: () -> () = {}
    var width: CGFloat
    var height: CGFloat
    var background: Color
    var image: String?
    var font: Font
    var strokeWidth: CGFloat
    var strokeColor: Color
    var loadingColor: Color
    @Binding var isLoading: Bool
    
    internal init(
        action: @escaping () -> Void,
        title: String,
        titleColor: Color = Color.white,
        width: CGFloat,
        height: CGFloat,
        backgroundColor: Color = Color.primary,
        image: String? = nil,
        font: Font = .semiBold(16),
        strokeWidth: CGFloat = 4,
        strokeColor: Color = .white,
        loadingColor: Color = Color.primary,
        isLoading: Binding<Bool>) {
            self.title = title
            self.titleColor = titleColor
            self.action = action
            self.width = width
            self.height = height
            self.image = image
            self._isLoading = isLoading
            self.background = backgroundColor
            self.font = font
            self.strokeWidth = strokeWidth
            self.strokeColor = strokeColor
            self.loadingColor = loadingColor
        }
    
    var body: some View {
        let style = CMButtonStyle(width: width,
                                  height: height,
                                  cornerRadius: height/2,
                                  backgroundColor: background,
                                  loadingColor: loadingColor,
                                  strokeWidth: strokeWidth,
                                  strokeColor: strokeColor)
        
        LoadingButton(action: {
            action()
        }, isLoading: $isLoading, style: style) {
            if let image = image {
                Image(image)
                    .frame(width: width, height: height)
                    .cornerRadius(height/2)
            } else {
                Text(title)
                    .foregroundColor(titleColor)
                    .font(font)
                    .frame(width: width, height: height)
                    .background(
                        background
                    )
                    .cornerRadius(height/2)
            }
        }
    }
}

struct ButtonWithLoader_Previews: PreviewProvider {
    static var previews: some View {
        ButtonWithLoader(action: { }, title: "Sign In", width: 300 - 50, height: 41, isLoading: .constant(false))
    }
}
