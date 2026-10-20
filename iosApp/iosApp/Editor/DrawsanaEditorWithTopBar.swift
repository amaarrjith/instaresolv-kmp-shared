//
//  DrawsanaEditorWithTopBar.swift
//  QualityExpertise
//
//  Created by Amarjith B on 05/03/26.
//


import UIKit
import SwiftUI
import Drawsana

class DrawsanaEditorWithTopBar: ViewController {

    // MARK: Inputs
    var baseImage: UIImage?
    var onDone: ((UIImage) -> Void)?
    var onCancel: (() -> Void)?
    var isSignature: Bool = false

    // MARK: Custom UI
    private let topBar = UIView()
    private let doneButton = UIButton(type: .system)
    private let cancelButton = UIButton(type: .system)

    // MARK: Button Spinner
    private let buttonSpinner = UIActivityIndicatorView(style: .medium)
    private var doneIcon: UIImage?

    private let mainToolBar = UIView()
    private let mainStack = UIStackView()

    // popup tag constants
    private var strokePopupTag: Int { 9991 }
    private var colorPopupTag: Int { 5555 }

    // popup bottom constraints so we can update when toggling
    private var strokePopupBottomConstraint: NSLayoutConstraint?
    private var colorPopupBottomConstraint: NSLayoutConstraint?

    private let toolTrayBar = UIView()
    private var toolTrayHeightConstraint: NSLayoutConstraint?

    // --- MAIN TOOLBAR ARROW (last button) ---
    private let toggleArrowButton = UIButton(type: .system)

    // --- TOOL STORAGE ---
    enum ToolType { case pen, eraser, text, select, ellipse, rect, arrow, line }

    private var currentTool: ToolType = .pen
    private var toolButtons: [ToolType: UIButton] = [:]   // For highlight

    // MARK: Init
    convenience init(image: UIImage, isSignature: Bool = false, onDone: ((UIImage)->Void)?, onCancel: (() -> Void)?) {
        self.init()
        self.baseImage = image
        self.isSignature = isSignature
        self.onDone = onDone
        self.onCancel = onCancel
    }

    override func loadView() {
        if let baseImage { self.imageView.image = baseImage }
        super.loadView()
    }

    override func viewSafeAreaInsetsDidChange() {
        super.viewSafeAreaInsetsDidChange()
        additionalSafeAreaInsets = UIEdgeInsets(top: -view.safeAreaInsets.top, left: 0, bottom: 0, right: 0)
    }

    // track toolTray state
    private var toolTrayHidden = false

    override func viewDidLoad() {
        super.viewDidLoad()

        view.insetsLayoutMarginsFromSafeArea = false

        // hide Drawsana default toolbar (we build custom)
        toolbarBackground.isHidden = true
        toolbarStackView.isHidden = true
        toolbarBackground.isUserInteractionEnabled = false
        toolbarStackView.isUserInteractionEnabled = false

        setupTopBar()
        setupMainToolbar()
        setupToolTrayToolbar()

        selectTool(.pen)   // Default tool
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

        // rotate done icon if needed
        doneButton.imageView?.transform = CGAffineTransform(rotationAngle: .pi)

        // update dynamic tool tray height
        let computed = max(90.0, view.bounds.height * 0.12)
        toolTrayHeightConstraint?.constant = CGFloat(computed)

        // ensure toggle arrow remains visible (arrow is inside main toolbar; we don't reposition)
        // If popups exist, update their bottom anchor target depending on toolTrayHidden
        repositionOpenPopups(animated: false)
        
        // bring important overlays to front
        view.bringSubviewToFront(topBar)
        view.bringSubviewToFront(mainToolBar)
        view.bringSubviewToFront(toolTrayBar)
    }

    // ============================================================
    // MARK: TOP BAR
    // ============================================================
    private func setupTopBar() {

        topBar.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        topBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(topBar)

        NSLayoutConstraint.activate([
            topBar.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor, constant: 0), // start below notch/status bar
            topBar.leftAnchor.constraint(equalTo: view.leftAnchor),
            topBar.rightAnchor.constraint(equalTo: view.rightAnchor),
            topBar.heightAnchor.constraint(equalTo: view.heightAnchor, multiplier: 0.08) // proportional height
        ])


        // CANCEL
        let cancelIcon = UIImage(named: "icBack")?
            .withRenderingMode(.alwaysTemplate)

        cancelButton.setImage(cancelIcon, for: .normal)
        cancelButton.tintColor = .white
        cancelButton.backgroundColor = .red
        cancelButton.translatesAutoresizingMaskIntoConstraints = false
        cancelButton.clipsToBounds = true
        cancelButton.addTarget(self, action: #selector(cancelPressed), for: .touchUpInside)

        topBar.addSubview(cancelButton)

        // DONE
        let doneIcon = UIImage(named: "icBack")?
            .withRenderingMode(.alwaysTemplate)
        self.doneIcon = doneIcon

        doneButton.setImage(doneIcon, for: .normal)
        doneButton.tintColor = .white
        doneButton.backgroundColor = UIColor(red: 0, green: 81/255, blue: 126/255, alpha: 0.85)
        doneButton.translatesAutoresizingMaskIntoConstraints = false
        doneButton.clipsToBounds = true
        doneButton.addTarget(self, action: #selector(donePressed), for: .touchUpInside)

        buttonSpinner.translatesAutoresizingMaskIntoConstraints = false
        buttonSpinner.color = .white
        buttonSpinner.hidesWhenStopped = true
        doneButton.addSubview(buttonSpinner)

        topBar.addSubview(doneButton)

        NSLayoutConstraint.activate([
            cancelButton.leftAnchor.constraint(equalTo: topBar.leftAnchor, constant: 30),
            cancelButton.bottomAnchor.constraint(equalTo: topBar.bottomAnchor, constant: -10),
            cancelButton.widthAnchor.constraint(equalToConstant: 44),
            cancelButton.heightAnchor.constraint(equalToConstant: 44),

            doneButton.rightAnchor.constraint(equalTo: topBar.rightAnchor, constant: -30),
            doneButton.bottomAnchor.constraint(equalTo: topBar.bottomAnchor, constant: -10),
            doneButton.widthAnchor.constraint(equalToConstant: 44),
            doneButton.heightAnchor.constraint(equalToConstant: 44),

            buttonSpinner.centerXAnchor.constraint(equalTo: doneButton.centerXAnchor),
            buttonSpinner.centerYAnchor.constraint(equalTo: doneButton.centerYAnchor)
        ])

        cancelButton.layer.cornerRadius = 22
        doneButton.layer.cornerRadius = 22
    }

    // ============================================================
    // MARK: MAIN TOOLBAR (with arrow as last button)
    // ============================================================
    private func setupMainToolbar() {

        mainToolBar.backgroundColor = UIColor.black.withAlphaComponent(0.9)
        mainToolBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(mainToolBar)

        NSLayoutConstraint.activate([
            mainToolBar.leftAnchor.constraint(equalTo: view.leftAnchor),
            mainToolBar.rightAnchor.constraint(equalTo: view.rightAnchor),
            mainToolBar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            mainToolBar.heightAnchor.constraint(equalToConstant: 90)
        ])

        mainStack.axis = .horizontal
        mainStack.distribution = .equalSpacing
        mainStack.alignment = .center
        mainStack.translatesAutoresizingMaskIntoConstraints = false
        mainToolBar.addSubview(mainStack)

        NSLayoutConstraint.activate([
            mainStack.leftAnchor.constraint(equalTo: mainToolBar.leftAnchor, constant: 20),
            mainStack.rightAnchor.constraint(equalTo: mainToolBar.rightAnchor, constant: -20),
            mainStack.centerYAnchor.constraint(equalTo: mainToolBar.centerYAnchor)
        ])

        // Buttons in main toolbar (arrow is last)
        // Note: we use Drawsana's built-in buttons (undoButton, redoButton, strokeColorButton, strokeWidthButton, toolButton)
        let buttons = [undoButton, redoButton, strokeColorButton, strokeWidthButton, toolButton, toggleArrowButton]

        for btn in buttons {
            styleMainButton(btn)
            mainStack.addArrangedSubview(btn)
        }

        // configure icons & actions
        undoButton.setImage(UIImage(systemName: "arrow.uturn.left"), for: .normal)
        redoButton.setImage(UIImage(systemName: "arrow.uturn.right"), for: .normal)

        // stroke color open palette
        strokeColorButton.addTarget(self, action: #selector(openStrokesColorMenu(_:)), for: .touchUpInside)

        // stroke width opens slider
        strokeWidthButton.addTarget(self, action: #selector(openStrokeSlider), for: .touchUpInside)

        toolButton.setImage(UIImage(systemName: "scribble"), for: .normal)

        // toggle arrow button appearance
        toggleArrowButton.setImage(UIImage(systemName: "chevron.compact.up"), for: .normal)
        toggleArrowButton.tintColor = .white
        toggleArrowButton.addTarget(self, action: #selector(toggleToolTrayFromMain), for: .touchUpInside)

        // ensure color bubble inner view is created
        updateStrokeColorBubble()
        updateStrokeWidthButton()
    }

    private func styleMainButton(_ button: UIButton) {
        button.setTitle("", for: .normal)
        button.backgroundColor = UIColor.white.withAlphaComponent(0.06)
        button.tintColor = .white

        button.layer.cornerRadius = 25
        button.clipsToBounds = true

        // consistent size
        button.widthAnchor.constraint(equalToConstant: 50).isActive = true
        button.heightAnchor.constraint(equalToConstant: 50).isActive = true

        // stroke color outer border removed; inner bubble differentiates
        if button == strokeColorButton {
            button.layer.borderWidth = 0
            button.layer.borderColor = UIColor.clear.cgColor
        }
    }

    // build inner bubble + white icon overlay for strokeColorButton
    private func updateStrokeColorBubble() {
        // keep outer button identical to others
        strokeColorButton.backgroundColor = drawingView.userSettings.strokeColor
        strokeColorButton.layer.cornerRadius = 25
        strokeColorButton.clipsToBounds = true

        // remove old inner bubble if any
        strokeColorButton.subviews.forEach { sub in
            if sub.tag == 777 || sub.tag == 778 { sub.removeFromSuperview() }
        }

        // inner bubble view
        let bubble = UIView()
        bubble.tag = 777
        bubble.layer.cornerRadius = 0
        bubble.translatesAutoresizingMaskIntoConstraints = false
        bubble.isUserInteractionEnabled = false
        strokeColorButton.addSubview(bubble)

        // overlay white icon
        let icon = UIImageView(image: UIImage(systemName: "paintpalette"))
        icon.tag = 778
        icon.tintColor = drawingView.userSettings.strokeColor == .white ? .black : .white
        icon.translatesAutoresizingMaskIntoConstraints = false
        icon.contentMode = .scaleAspectFit
        icon.isUserInteractionEnabled = false
        strokeColorButton.addSubview(icon)

        NSLayoutConstraint.activate([
            bubble.centerXAnchor.constraint(equalTo: strokeColorButton.centerXAnchor),
            bubble.centerYAnchor.constraint(equalTo: strokeColorButton.centerYAnchor),
            bubble.widthAnchor.constraint(equalToConstant: 30),
            bubble.heightAnchor.constraint(equalToConstant: 30),

            icon.centerXAnchor.constraint(equalTo: strokeColorButton.centerXAnchor),
            icon.centerYAnchor.constraint(equalTo: strokeColorButton.centerYAnchor),
            icon.widthAnchor.constraint(equalToConstant: 30),
            icon.heightAnchor.constraint(equalToConstant: 30)
        ])
    }

    private func updateCurrentToolIcon(_ type: ToolType) {
        switch type {
        case .pen:     toolButton.setImage(UIImage(systemName: "pencil"), for: .normal)
        case .text:    toolButton.setImage(UIImage(systemName: "textformat"), for: .normal)
        case .select:  toolButton.setImage(UIImage(systemName: "cursorarrow"), for: .normal)
        case .ellipse: toolButton.setImage(UIImage(systemName: "circle"), for: .normal)
        case .rect:    toolButton.setImage(UIImage(systemName: "rectangle"), for: .normal)
        case .arrow:   toolButton.setImage(UIImage(systemName: "arrow.up.right"), for: .normal)
        case .line:    toolButton.setImage(UIImage(systemName: "line.diagonal"), for: .normal)
        case .eraser:  toolButton.setImage(UIImage(systemName: "eraser.fill"), for: .normal)
        }
    }

    private func updateStrokeWidthButton() {
        let width = drawingView.userSettings.strokeWidth
        strokeWidthButton.setTitle("\(Int(width))", for: .normal)
        strokeWidthButton.setTitleColor(.white, for: .normal)
        strokeWidthButton.titleLabel?.font = .systemFont(ofSize: 14, weight: .semibold)
    }

    // ============================================================
    // MARK: TOOL TRAY (scrolling) - Dynamic height
    // ============================================================
    private func setupToolTrayToolbar() {

        toolTrayBar.backgroundColor = UIColor.black.withAlphaComponent(0.95)
        toolTrayBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(toolTrayBar)

        // dynamic height constraint (initial value; updated later)
        let initialHeight = max(90.0, view.bounds.height * 0.12)
        toolTrayHeightConstraint = toolTrayBar.heightAnchor.constraint(equalToConstant: CGFloat(initialHeight))
        toolTrayHeightConstraint?.isActive = true

        NSLayoutConstraint.activate([
            toolTrayBar.leftAnchor.constraint(equalTo: view.leftAnchor),
            toolTrayBar.rightAnchor.constraint(equalTo: view.rightAnchor),
            toolTrayBar.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor)
        ])

        // scroll area inside tool tray
        let scroll = UIScrollView()
        scroll.translatesAutoresizingMaskIntoConstraints = false
        scroll.showsHorizontalScrollIndicator = false
        scroll.alwaysBounceHorizontal = true
        toolTrayBar.addSubview(scroll)

        NSLayoutConstraint.activate([
            scroll.leftAnchor.constraint(equalTo: toolTrayBar.leftAnchor),
            scroll.rightAnchor.constraint(equalTo: toolTrayBar.rightAnchor),
            scroll.topAnchor.constraint(equalTo: toolTrayBar.topAnchor),
            scroll.bottomAnchor.constraint(equalTo: toolTrayBar.bottomAnchor)
        ])

        // content stack (closer spacing)
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 10   // tighter spacing for denser tool tray
        stack.alignment = .center
        stack.translatesAutoresizingMaskIntoConstraints = false
        scroll.addSubview(stack)

        NSLayoutConstraint.activate([
            stack.leftAnchor.constraint(equalTo: scroll.leftAnchor, constant: 20),
            stack.rightAnchor.constraint(equalTo: scroll.rightAnchor, constant: -20),
            stack.topAnchor.constraint(equalTo: scroll.topAnchor),
            stack.bottomAnchor.constraint(equalTo: scroll.bottomAnchor),
            stack.heightAnchor.constraint(equalTo: scroll.heightAnchor)
        ])

        if isSignature {
            addTool(type: .pen,    icon: "pencil",      title: "Pen",   to: stack)
            addTool(type: .eraser, icon: "eraser.fill", title: "Erase", to: stack)
        } else {
            addTool(type: .pen,     icon: "pencil",         title: "Pen",     to: stack)
            addTool(type: .eraser,  icon: "eraser.fill",    title: "Erase",   to: stack)
            addTool(type: .text,    icon: "textformat",     title: "Text",    to: stack)
            addTool(type: .select,  icon: "cursorarrow",    title: "Select",  to: stack)
            addTool(type: .ellipse, icon: "circle",         title: "Circle",  to: stack)
            addTool(type: .rect,    icon: "rectangle",      title: "Rect",    to: stack)
            addTool(type: .arrow,   icon: "arrow.up.right", title: "Arrow",   to: stack)
            addTool(type: .line,    icon: "line.diagonal",  title: "Line",    to: stack)
        }
    }

    private func addTool(type: ToolType, icon: String, title: String, to stack: UIStackView) {

        let button = UIButton(type: .system)
        button.setImage(UIImage(systemName: icon), for: .normal)
        button.tintColor = .white
        button.translatesAutoresizingMaskIntoConstraints = false
        button.widthAnchor.constraint(equalToConstant: 45).isActive = true
        button.heightAnchor.constraint(equalToConstant: 45).isActive = true
        button.layer.cornerRadius = 22.5
        button.clipsToBounds = true

        button.tag = type.hashValue
        button.addTarget(self, action: #selector(toolPressed(_:)), for: .touchUpInside)

        // LABEL
        let label = UILabel()
        label.text = title
        label.font = .systemFont(ofSize: 10, weight: .regular)
        label.textColor = .white
        label.textAlignment = .center

        // VERTICAL STACK (narrower for denser layout)
        let container = UIStackView(arrangedSubviews: [button, label])
        container.axis = .vertical
        container.spacing = 6
        container.alignment = .center
        container.translatesAutoresizingMaskIntoConstraints = false
        container.widthAnchor.constraint(equalToConstant: 50).isActive = true

        stack.addArrangedSubview(container)

        toolButtons[type] = button
    }

    // Highlight selected tool
    private func highlightTool(_ type: ToolType) {
        toolButtons.forEach { (_, btn) in
            btn.layer.borderWidth = 0
            btn.backgroundColor = .clear
        }
        if let btn = toolButtons[type] {
            btn.layer.borderWidth = 2
            btn.layer.borderColor = UIColor.systemBlue.cgColor
            btn.backgroundColor = UIColor.white.withAlphaComponent(0.12)
        }
    }

    // ============================================================
    // TOOL CHANGES
    // ============================================================
    @objc private func toolPressed(_ sender: UIButton) {
        guard let type = toolButtons.first(where: { $0.value == sender })?.key else { return }
        selectTool(type)
    }

    private func selectTool(_ type: ToolType) {

        // If signature mode, allow ONLY pen + eraser
        if isSignature && !(type == .pen || type == .eraser) {
            return
        }

        currentTool = type
        highlightTool(type)
        updateCurrentToolIcon(type)

        switch type {
        case .pen:     drawingView.set(tool: PenTool())
        case .eraser:  drawingView.set(tool: EraserTool())
        case .text:    drawingView.set(tool: textTool)
        case .select:  drawingView.set(tool: selectionTool)
        case .ellipse: drawingView.set(tool: EllipseTool())
        case .rect:    drawingView.set(tool: RectTool())
        case .arrow:   drawingView.set(tool: ArrowTool())
        case .line:    drawingView.set(tool: LineTool())
        }
    }


    // ============================================================
    // BUTTON SPINNER
    // ============================================================
    private func showLoading(_ show: Bool) {
        if show {
            doneButton.setImage(nil, for: .normal)
            buttonSpinner.startAnimating()
            view.isUserInteractionEnabled = false
            doneButton.isEnabled = false
            cancelButton.isEnabled = false
        } else {
            doneButton.setImage(doneIcon, for: .normal)
            doneButton.imageView?.transform = CGAffineTransform(rotationAngle: .pi)
            buttonSpinner.stopAnimating()
            view.isUserInteractionEnabled = true
            doneButton.isEnabled = true
            cancelButton.isEnabled = true
        }
    }

    // ============================================================
    // ACTIONS
    // ============================================================
    @objc private func cancelPressed() { onCancel?() }

    @objc private func donePressed() {
        showLoading(true)

        // Give UIKit a runloop tick so the loading HUD displays immediately before heavy rendering
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.05) { [weak self] in
            guard let self = self else { return }
            let final = self.drawingView.render(over: self.imageView.image) ?? (self.baseImage ?? UIImage())
            self.onDone?(final)
        }
    }

    // Toggle tool tray from the main toolbar arrow button
    @objc private func toggleToolTrayFromMain() {
        toggleToolTray(animated: true)
    }

    // central toggle: animates and repositions popups
    private func toggleToolTray(animated: Bool) {
        toolTrayHidden.toggle()

        // remove any open popups for cleanliness OR animate them to new position
        // We'll animate the toolTray transform & alpha and move popups to new anchor.
        let h = toolTrayHeightConstraint?.constant ?? 110

        // Prepare the popup bottom constraint target: when hidden -> anchor mainToolBar.top, when visible -> toolTrayBar.top
        let newBottomConstantForStroke: CGFloat
        let newBottomConstantForColor: CGFloat
        let anchorViewForBottom: UIView

        if toolTrayHidden {
            // moving toolTray down (offscreen under main toolbar): we want popups above main toolbar
            anchorViewForBottom = mainToolBar
            newBottomConstantForStroke = -60 - 10       // slider height (60) + spacing
            newBottomConstantForColor = -10             // color sits directly on top of main toolbar
        } else {
            // tool tray visible, popups should anchor above toolTray
            anchorViewForBottom = toolTrayBar
            newBottomConstantForStroke = -70 - 10      // slider above color (70) + spacing
            newBottomConstantForColor =  -10
        }

        // animate tool tray slide & fade
        let targetTransform: CGAffineTransform = toolTrayHidden ? CGAffineTransform(translationX: 0, y: h) : .identity
        let toolTrayAlpha: CGFloat = toolTrayHidden ? 0.0 : 1.0
        let rotateAngle: CGFloat = toolTrayHidden ? .pi : 0.0

        // update arrow image rotation smoothly
        UIView.animate(withDuration: animated ? 0.35 : 0.0, delay: 0, options: [.curveEaseInOut]) {
            self.toolTrayBar.transform = targetTransform
            self.toolTrayBar.alpha = toolTrayAlpha
            self.toggleArrowButton.imageView?.transform = CGAffineTransform(rotationAngle: rotateAngle)
        }

        // update popup constraints and animate layoutIfNeeded
        // stroke popup
        if let stroke = view.viewWithTag(strokePopupTag) {
            if let c = strokePopupBottomConstraint {
                // remove old constraint and create a new one relative to chosen anchor
                NSLayoutConstraint.deactivate([c])
            }
            stroke.translatesAutoresizingMaskIntoConstraints = false

            // find the bottom constraint we will use next
            if toolTrayHidden {
                // anchor to mainToolBar.top
                let newC = stroke.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: newBottomConstantForStroke)
                newC.isActive = true
                strokePopupBottomConstraint = newC
            } else {
                let newC = stroke.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: newBottomConstantForStroke)
                newC.isActive = true
                strokePopupBottomConstraint = newC
            }

            UIView.animate(withDuration: animated ? 0.35 : 0.0, delay: 0, options: [.curveEaseInOut]) {
                self.view.layoutIfNeeded()
                stroke.alpha = toolTrayAlpha == 0 ? 0.0 : 1.0
            }
        }

        // color popup
        if let color = view.viewWithTag(colorPopupTag) {
            if let c = colorPopupBottomConstraint {
                NSLayoutConstraint.deactivate([c])
            }
            color.translatesAutoresizingMaskIntoConstraints = false

            if toolTrayHidden {
                let newC = color.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: newBottomConstantForColor)
                newC.isActive = true
                colorPopupBottomConstraint = newC
            } else {
                let newC = color.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: newBottomConstantForColor)
                newC.isActive = true
                colorPopupBottomConstraint = newC
            }

            UIView.animate(withDuration: animated ? 0.35 : 0.0, delay: 0, options: [.curveEaseInOut]) {
                self.view.layoutIfNeeded()
                color.alpha = toolTrayAlpha == 0 ? 0.0 : 1.0
            }
        }
    }

    // reposition open popups (used on rotations / layout changes); animated optional
    private func repositionOpenPopups(animated: Bool) {
        // if stroke popup exists, update its bottom anchor depending on toolTrayHidden
        if let stroke = view.viewWithTag(strokePopupTag) {
            if let c = strokePopupBottomConstraint { NSLayoutConstraint.deactivate([c]) }
            stroke.translatesAutoresizingMaskIntoConstraints = false
            if toolTrayHidden {
                let newC = stroke.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: -60 - 10)
                newC.isActive = true
                strokePopupBottomConstraint = newC
            } else {
                let newC = stroke.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: -70 - 10)
                newC.isActive = true
                strokePopupBottomConstraint = newC
            }
        }

        if let color = view.viewWithTag(colorPopupTag) {
            if let c = colorPopupBottomConstraint { NSLayoutConstraint.deactivate([c]) }
            color.translatesAutoresizingMaskIntoConstraints = false
            if toolTrayHidden {
                let newC = color.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: -10)
                newC.isActive = true
                colorPopupBottomConstraint = newC
            } else {
                let newC = color.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: -10)
                newC.isActive = true
                colorPopupBottomConstraint = newC
            }
        }

        if animated {
            UIView.animate(withDuration: 0.28) { self.view.layoutIfNeeded() }
        } else {
            view.layoutIfNeeded()
        }
    }
}

// MARK: — Popups (slider + color palette)
extension DrawsanaEditorWithTopBar {

    // ============================================================
    // FULL-WIDTH STROKE WIDTH SLIDER (above the color palette)
    // ============================================================
    func showStrokeWidthSlider() {

        // toggle remove
        if let existing = view.viewWithTag(strokePopupTag) {
            existing.removeFromSuperview()
            strokePopupBottomConstraint = nil
            return
        }

        let popup = UIView()
        popup.tag = strokePopupTag
        popup.backgroundColor = UIColor.black.withAlphaComponent(0.92)
        popup.translatesAutoresizingMaskIntoConstraints = false

        let slider = UISlider()
        slider.minimumValue = 1
        slider.maximumValue = 40
        slider.value = Float(drawingView.userSettings.strokeWidth)
        slider.translatesAutoresizingMaskIntoConstraints = false
        slider.addTarget(self, action: #selector(strokeWidthChanged(_:)), for: .valueChanged)
        popup.addSubview(slider)

        NSLayoutConstraint.activate([
            slider.leftAnchor.constraint(equalTo: popup.leftAnchor, constant: 20),
            slider.rightAnchor.constraint(equalTo: popup.rightAnchor, constant: -20),
            slider.centerYAnchor.constraint(equalTo: popup.centerYAnchor)
        ])

        view.addSubview(popup)

        NSLayoutConstraint.activate([
            popup.leftAnchor.constraint(equalTo: view.leftAnchor),
            popup.rightAnchor.constraint(equalTo: view.rightAnchor),
            popup.heightAnchor.constraint(equalToConstant: 60)
        ])

        // ---- ANCHOR LOGIC ----
        let spacing: CGFloat = 10

        if let colorPalette = view.viewWithTag(colorPopupTag) {
            // Slider goes above color palette
            let c = popup.bottomAnchor.constraint(equalTo: colorPalette.topAnchor, constant: -spacing)
            c.isActive = true
            strokePopupBottomConstraint = c
        } else {
            // Slider directly above toolbar
            if toolTrayHidden {
                let c = popup.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: -spacing)
                c.isActive = true
                strokePopupBottomConstraint = c
            } else {
                let c = popup.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: -spacing)
                c.isActive = true
                strokePopupBottomConstraint = c
            }
        }

        // animation
        popup.alpha = 0
        popup.transform = CGAffineTransform(translationX: 0, y: 8)
        UIView.animate(withDuration: 0.22) {
            popup.alpha = 1
            popup.transform = .identity
        }
    }


    @objc private func strokeWidthChanged(_ sender: UISlider) {
        drawingView.userSettings.strokeWidth = CGFloat(sender.value)
        updateStrokeWidthButton()
    }

    @objc private func openStrokeSlider() {
        showStrokeWidthSlider()
    }

    // ============================================================
    // FULL-WIDTH COLOR PALETTE (full width, scrollable)
    // ============================================================
    @objc func openStrokesColorMenu(_ sender: UIButton) {
        showColorPalette()
    }

    func showColorPalette() {

        // Toggle: remove if exists
        if let existing = view.viewWithTag(colorPopupTag) {
            existing.removeFromSuperview()
            colorPopupBottomConstraint = nil
            return
        }

        view.viewWithTag(colorPopupTag)?.removeFromSuperview()

        let popup = UIView()
        popup.tag = colorPopupTag
        popup.backgroundColor = UIColor.black.withAlphaComponent(0.92)
        popup.translatesAutoresizingMaskIntoConstraints = false

        // Scroll View
        let scrollView = UIScrollView()
        scrollView.showsHorizontalScrollIndicator = false
        scrollView.translatesAutoresizingMaskIntoConstraints = false
        popup.addSubview(scrollView)

        NSLayoutConstraint.activate([
            scrollView.leftAnchor.constraint(equalTo: popup.leftAnchor, constant: 12),
            scrollView.rightAnchor.constraint(equalTo: popup.rightAnchor, constant: -12),
            scrollView.topAnchor.constraint(equalTo: popup.topAnchor),
            scrollView.bottomAnchor.constraint(equalTo: popup.bottomAnchor)
        ])

        // Horizontal Stack
        let stack = UIStackView()
        stack.axis = .horizontal
        stack.spacing = 16
        stack.alignment = .center
        stack.translatesAutoresizingMaskIntoConstraints = false
        scrollView.addSubview(stack)

        NSLayoutConstraint.activate([
            stack.leftAnchor.constraint(equalTo: scrollView.leftAnchor),
            stack.rightAnchor.constraint(equalTo: scrollView.rightAnchor),
            stack.topAnchor.constraint(equalTo: scrollView.topAnchor),
            stack.bottomAnchor.constraint(equalTo: scrollView.bottomAnchor),
            stack.heightAnchor.constraint(equalTo: scrollView.heightAnchor)
        ])

        // STANDARD COLORS
        let colors: [UIColor] = [
            .white, .black, .red, .orange, .yellow, .green,
            .systemTeal, .cyan, .blue,
            .systemIndigo, .purple, .systemPink, .brown, .gray
        ]

        for color in colors {
            let btn = UIButton(type: .system)
            btn.backgroundColor = color
            btn.layer.cornerRadius = 18
            btn.clipsToBounds = true
            btn.translatesAutoresizingMaskIntoConstraints = false
            btn.widthAnchor.constraint(equalToConstant: 36).isActive = true
            btn.heightAnchor.constraint(equalToConstant: 36).isActive = true

            btn.addAction(UIAction { _ in
                self.selectStrokeColor(color)
            }, for: .touchUpInside)

            stack.addArrangedSubview(btn)
        }

        view.addSubview(popup)

        // FIXED HEIGHT
        NSLayoutConstraint.activate([
            popup.leftAnchor.constraint(equalTo: view.leftAnchor),
            popup.rightAnchor.constraint(equalTo: view.rightAnchor),
            popup.heightAnchor.constraint(equalToConstant: 70)
        ])

        // ---- BOTTOM ANCHOR LOGIC ----
        let spacing: CGFloat = 10

        if toolTrayHidden {
            // anchored above main toolbar
            let c = popup.bottomAnchor.constraint(equalTo: mainToolBar.topAnchor, constant: -spacing)
            c.isActive = true
            colorPopupBottomConstraint = c
        } else {
            // anchored above tool tray
            let c = popup.bottomAnchor.constraint(equalTo: toolTrayBar.topAnchor, constant: -spacing)
            c.isActive = true
            colorPopupBottomConstraint = c
        }

        // animate
        popup.alpha = 0
        popup.transform = CGAffineTransform(translationX: 0, y: 8)
        UIView.animate(withDuration: 0.22) {
            popup.alpha = 1
            popup.transform = .identity
        }
    }


    func selectStrokeColor(_ color: UIColor) {
        drawingView.userSettings.strokeColor = color

        // update inner bubble on main button
        updateStrokeColorBubble()

        // close palette
        if let popup = view.viewWithTag(colorPopupTag) {
            UIView.animate(withDuration: 0.16, animations: {
                popup.alpha = 0
            }) { _ in
                popup.removeFromSuperview()
                self.colorPopupBottomConstraint = nil
            }
        }
    }
}
