//
//  ToolPickerViewControllerDelegate.swift
//  QualityExpertise
//
//  Created by Amarjith B on 05/03/26.
//


import Drawsana
import UIKit

protocol ToolPickerViewControllerDelegate: AnyObject {
    func toolPickerViewControllerDidPick(tool: DrawingTool)
}

class ToolPickerViewController: UIViewController {

    let tools: [DrawingTool]
    weak var delegate: ToolPickerViewControllerDelegate?

    private var selectedButton: UIButton?

    init(tools: [DrawingTool], delegate: ToolPickerViewControllerDelegate) {
        self.tools = tools
        self.delegate = delegate
        super.init(nibName: nil, bundle: nil)

        preferredContentSize = CGSize(
            width: 150,
            height: tools.count * 58 + 20
        )
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func loadView() {

        // Blur background (iOS 13+)
        let blurView = UIVisualEffectView(effect: UIBlurEffect(style: .systemUltraThinMaterial))
        blurView.layer.cornerRadius = 20
        blurView.clipsToBounds = true

        let stack = UIStackView()
        stack.axis = .vertical
        stack.spacing = 12
        stack.alignment = .fill
        stack.distribution = .equalSpacing
        stack.translatesAutoresizingMaskIntoConstraints = false

        // Buttons for each tool
        tools.enumerated().forEach { index, tool in
            let button = createToolButton(
                title: tool.name,
                icon: iconForTool(tool)
            )
            button.tag = index
            button.addTarget(self, action: #selector(selectTool(_:)), for: .touchUpInside)
            stack.addArrangedSubview(button)
        }

        blurView.contentView.addSubview(stack)

        NSLayoutConstraint.activate([
            stack.topAnchor.constraint(equalTo: blurView.contentView.topAnchor, constant: 16),
            stack.leadingAnchor.constraint(equalTo: blurView.contentView.leadingAnchor, constant: 16),
            stack.trailingAnchor.constraint(equalTo: blurView.contentView.trailingAnchor, constant: -16),
            stack.bottomAnchor.constraint(equalTo: blurView.contentView.bottomAnchor, constant: -16)
        ])

        self.view = blurView
    }

    // MARK: - Button Creation

    private func createToolButton(title: String, icon: UIImage?) -> UIButton {

        let button = UIButton(type: .custom)
        button.translatesAutoresizingMaskIntoConstraints = false

        button.backgroundColor = UIColor(white: 0.95, alpha: 1)
        button.layer.cornerRadius = 22
        button.layer.borderWidth = 1
        button.layer.borderColor = UIColor(white: 0.85, alpha: 1).cgColor

        // Icon + title layout
        button.setImage(icon, for: .normal)
        button.tintColor = .black
        button.setTitle("  \(title)", for: .normal)   // spacing
        button.setTitleColor(.black, for: .normal)

        // Poppins font
        button.titleLabel?.font = UIFont(name: "Poppins-SemiBold", size: 15)

        button.contentHorizontalAlignment = .left
        button.imageView?.contentMode = .scaleAspectFit

        // Padding inside
        button.contentEdgeInsets = UIEdgeInsets(top: 0, left: 14, bottom: 0, right: 14)
        button.heightAnchor.constraint(equalToConstant: 44).isActive = true

        return button
    }

    // MARK: - Icons for Tools

    private func iconForTool(_ tool: DrawingTool) -> UIImage? {
        switch tool {
        case is PenTool:
            return UIImage(systemName: "pencil.tip")
        case is LineTool:
            return UIImage(systemName: "line.diagonal")
        case is RectTool:
            return UIImage(systemName: "square.on.square")
        case is EllipseTool:
            return UIImage(systemName: "circle")
        case is TextTool:
            return UIImage(systemName: "textformat")
        case is EraserTool:
            return UIImage(systemName: "eraser")
        default:
            return UIImage(systemName: "square.dashed")
        }
    }

    // MARK: - Selection Logic

    @objc private func selectTool(_ sender: UIButton) {

        // Reset previous
        if let old = selectedButton {
            old.backgroundColor = UIColor(white: 0.95, alpha: 1)
            old.tintColor = .black
            old.setTitleColor(.black, for: .normal)
            old.layer.borderColor = UIColor(white: 0.85, alpha: 1).cgColor
        }

        // Highlight current
        sender.backgroundColor = UIColor.systemBlue.withAlphaComponent(0.25)
        sender.tintColor = .systemBlue
        sender.setTitleColor(.systemBlue, for: .normal)
        sender.layer.borderColor = UIColor.systemBlue.cgColor

        selectedButton = sender

        delegate?.toolPickerViewControllerDidPick(tool: tools[sender.tag])
    }
}
