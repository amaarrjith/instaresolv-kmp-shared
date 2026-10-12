import UIKit
import SwiftUI
import Shared

enum ImageFlowState: Identifiable {
    case crop(UIImage)
    case annotate(UIImage)

    var id: String {
        switch self {
        case .crop: return "crop"
        case .annotate: return "annotate"
        }
    }
}

class SwiftImageEditorState: ObservableObject {
    static let shared = SwiftImageEditorState()

    @Published var flow: ImageFlowState? = nil
    var completionHandler: ((KotlinByteArray?) -> Void)? = nil
}

class SwiftImageEditorBridge: IosImageEditorBridge {
    func launchEditor(imageBytes: KotlinByteArray, onComplete: @escaping (KotlinByteArray?) -> Void) {
        DispatchQueue.main.async {
            let data = imageBytes.toData()
            guard let image = UIImage(data: data) else {
                onComplete(nil)
                return
            }
            SwiftImageEditorState.shared.completionHandler = onComplete
            SwiftImageEditorState.shared.flow = .crop(image)
        }
    }
}

extension KotlinByteArray {
    func toData() -> Data {
        let count = Int(self.size)
        var data = Data(count: count)
        data.withUnsafeMutableBytes { (rawBufferPointer: UnsafeMutableRawBufferPointer) in
            guard let baseAddress = rawBufferPointer.bindMemory(to: Int8.self).baseAddress else { return }
            for i in 0..<count {
                baseAddress[i] = self.get(index: Int32(i))
            }
        }
        return data
    }

    static func from(data: Data) -> KotlinByteArray {
        let byteArray = KotlinByteArray(size: Int32(data.count))
        data.withUnsafeBytes { (rawBufferPointer: UnsafeRawBufferPointer) in
            guard let baseAddress = rawBufferPointer.bindMemory(to: Int8.self).baseAddress else { return }
            for i in 0..<data.count {
                byteArray.set(index: Int32(i), value: baseAddress[i])
            }
        }
        return byteArray
    }
}
