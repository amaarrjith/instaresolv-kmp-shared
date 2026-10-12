//
//  TOCropView.swift
//  QualityExpertise
//
//  Created by Amarjith B on 05/03/26.
//


import SwiftUI
import TOCropViewController

struct TOCropView: UIViewControllerRepresentable {

    var image: UIImage
    var onCropped: (UIImage) -> Void
    var onCancel: () -> Void

    class Coordinator: NSObject, TOCropViewControllerDelegate {
        var parent: TOCropView

        init(parent: TOCropView) {
            self.parent = parent
        }

        func cropViewController(_ cropViewController: TOCropViewController,
                                didCropTo image: UIImage,
                                with cropRect: CGRect,
                                angle: Int) {
            cropViewController.dismiss(animated: true) {
                self.parent.onCropped(image)
            }
        }

        func cropViewController(_ cropViewController: TOCropViewController,
                                didFinishCancelled cancelled: Bool) {
            cropViewController.dismiss(animated: true) {
                self.parent.onCancel()
            }
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(parent: self)
    }

    func makeUIViewController(context: Context) -> TOCropViewController {
        let cropVC = TOCropViewController(image: image)
        cropVC.delegate = context.coordinator

        // Aspect Ratios
        cropVC.allowedAspectRatios = [
            1,4,2,7
        ]

        return cropVC
    }





    func updateUIViewController(_ uiViewController: TOCropViewController, context: Context) {}
}

