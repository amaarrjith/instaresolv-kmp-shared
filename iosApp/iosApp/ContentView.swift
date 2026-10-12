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
                            editorState.flow = nil
                            if let data = finalImg.jpegData(compressionQuality: 0.9) {
                                editorState.completionHandler?(KotlinByteArray.from(data: data))
                            } else {
                                editorState.completionHandler?(nil)
                            }
                            editorState.completionHandler = nil
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
