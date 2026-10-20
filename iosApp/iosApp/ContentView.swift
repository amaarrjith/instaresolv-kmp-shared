import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    @ObservedObject var editorState = SwiftImageEditorState.shared

    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .fullScreenCover(item: $editorState.flow) { flow in
                switch flow {
                case .crop(let image):
                    TOCropView(
                        image: image,
                        onCropped: { cropped in
                            editorState.flow = .annotate(cropped)
                        },
                        onCancel: {
                            editorState.flow = nil
                            editorState.completionHandler?(nil)
                            editorState.completionHandler = nil
                        }
                    )
                    .ignoresSafeArea()

                case .annotate(let image):
                    ModernDrawsanaWrapper(
                        image: image,
                        onDone: { finalImg in
                            DispatchQueue.global(qos: .userInitiated).async {
                                // Downscale to 1920 max if needed to speed up encoding and memory
                                let targetImg: UIImage
                                let maxDim: CGFloat = 1920
                                let currentMax = max(finalImg.size.width, finalImg.size.height)
                                if currentMax > maxDim {
                                    let scale = maxDim / currentMax
                                    let newSize = CGSize(width: finalImg.size.width * scale, height: finalImg.size.height * scale)
                                    UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
                                    finalImg.draw(in: CGRect(origin: .zero, size: newSize))
                                    targetImg = UIGraphicsGetImageFromCurrentImageContext() ?? finalImg
                                    UIGraphicsEndImageContext()
                                } else {
                                    targetImg = finalImg
                                }

                                let data = targetImg.jpegData(compressionQuality: 0.8)
                                let kotlinBytes = data.map { KotlinByteArray.from(data: $0) }

                                DispatchQueue.main.async {
                                    editorState.flow = nil
                                    editorState.completionHandler?(kotlinBytes)
                                    editorState.completionHandler = nil
                                }
                            }
                        },
                        onCancel: {
                            editorState.flow = nil
                            editorState.completionHandler?(nil)
                            editorState.completionHandler = nil
                        }
                    )
                    .ignoresSafeArea()
                }
            }
    }
}
