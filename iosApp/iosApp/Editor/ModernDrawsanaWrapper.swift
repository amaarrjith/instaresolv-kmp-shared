//
//  ModernDrawsanaWrapper.swift
//  QualityExpertise
//
//  Created by Amarjith B on 05/03/26.
//


import SwiftUI

struct ModernDrawsanaWrapper: UIViewControllerRepresentable {
    let image: UIImage
    var isSignature: Bool = false
    var onDone: (UIImage) -> Void
    var onCancel: () -> Void
    
    func makeUIViewController(context: Context) -> DrawsanaEditorWithTopBar {
        return DrawsanaEditorWithTopBar(
            image: image,
            isSignature: isSignature,
            onDone: { img in
                onDone(img)        // ← pass the final image
            },
            onCancel: {
                onCancel()
            }
        )
    }
    
    func updateUIViewController(_ uiViewController: DrawsanaEditorWithTopBar, context: Context) {}
}