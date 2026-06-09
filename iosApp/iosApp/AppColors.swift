//
//  AppColors.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  AppColors.swift
//  InstaResolv
//
//  Consolidated color constants for the InstaResolv app.
//

import SwiftUI

extension Color {
    
    // MARK: - Core Theme Colors
    static let splashPrimary = Color(hex: "#F5831A")       // Bright orange — top of splash gradient
    static let splashSecondary = Color(hex: "#D42027")      // Deep red — bottom of splash gradient
    static let primary = Color(hex: "#D42027")              // Main brand red
    static let placeHolder = Color(hex: "#F6F6F6")
    static let gray = Color(hex: "#EFF0F5")                 // Backgrounds and subtle UI elements
    static let grayDots = Color(hex: "#D9D9D9")
    static let blueTheme = Color(hex: "#2C4161")
    static let blackTheme = Color(hex: "#1F2024")
    static let greenTheme = Color(hex: "#00BA00")
    static let commonGreen = Color(hex: "#00A600")
    static let commonRed = Color(hex: "#F44336")
    
    // MARK: - Toast & Notification Colors
    static let toastSuccess = Color(hex: "#22C55E")
    static let toastWarning = Color(hex: "#F59E0B")
    static let toastInfo = Color(hex: "#3B82F6")
    static let toastSuccessBackground = Color(hex: "#F0FDF4")
    static let toastWarningBackground = Color(hex: "#FFFBEB")
    static let toastErrorBackground = Color(hex: "#FEF2F2")
    static let toastInfoBackground = Color(hex: "#EFF6FF")
    
    // MARK: - UI Component & Design Colors
    static let alertBlue = Color(hex: "#2B65C0")
    static let alertTextSecondary = Color(hex: "#71747A")
    static let linkBlue = Color(hex: "#2062C5")
    static let alertCancelRed = Color(hex: "#D62128")
    static let translatedTextBlue = Color(hex: "#2E6AC6")
    static let inactiveTabGray = Color(hex: "#9E9E9E")
    static let contentGray = Color(hex: "#8F9098")
    static let borderGray = Color(hex: "#D1D1D1")
    
    // MARK: - Extended Palette
    // Greys
    static let darkTextLabel = Color(hex: "#1D2129")
    static let mediumGray = Color(hex: "#727272")
    static let mutedGray = Color(hex: "#9DA1A6")
    static let iconGray = Color(hex: "#A0A0A0")
    static let headerGray = Color(hex: "#A0A5AD")
    static let borderLightGray = Color(hex: "#AFB1B4")
    static let tertiaryGray = Color(hex: "#B0B0B0")
    static let inputFieldBackground = Color(hex: "#BDBDBD")
    static let disabledTextGray = Color(hex: "#C4C4C4")
    static let textFieldBorder = Color(hex: "#D2D2D2")
    static let cardBorder = Color(hex: "#DADADA")
    static let separatorGray = Color(hex: "#E5E5E5")
    static let dividerGray = Color(hex: "#E5E7EB")
    static let lightGrayBackground = Color(hex: "#E7E8EC")
    static let sectionBackground = Color(hex: "#EEF0F5")
    static let expandedCellBackground = Color(hex: "#F1F1F1")
    static let popupBackground = Color(hex: "#F1F4F9")
    static let sheetBackground = Color(hex: "#F4F5F7")
    static let fieldBackground = Color(hex: "#F5F5F5")
    
    // Blues
    static let accentBlue = Color(hex: "#4478E6")
    static let secondaryBlue = Color(hex: "#6B7A99")
    
    // MARK: - Pending Action Status Colors
    /// Teal theme
    static let pendingStatusTeal = Color(hex: "#1ABC9C")
    static let pendingStatusTealBackground = Color(hex: "#E8F8F5")
    
    /// Sky Blue theme
    static let pendingStatusSkyBlue = Color(hex: "#00BFFF")
    static let pendingStatusSkyBlueBackground = Color(hex: "#EAF6FF")
    
    /// Salmon theme
    static let pendingStatusSalmon = Color(hex: "#FA8072")
    static let pendingStatusSalmonBackground = Color(hex: "#FFE4E1")
    
    /// Blue theme
    static let pendingStatusBlue = Color(hex: "#6495ED")
    static let pendingStatusBlueBackground = Color(hex: "#D6EAF8")
    
    /// Orange theme
    static let pendingStatusOrange = Color(hex: "#FFB347")
    static let pendingStatusOrangeBackground = Color(hex: "#FFE5CC")
    
    // Observations
    static let pendingCloseoutOrange = Color(hex: "#f6a03a")
    
    // MARK: - Miscellaneous Design Colors
    static let drawsanaDoneButton = Color(hex: "#00517E")
    static let warningYellow = Color(hex: "#FCB922")
    static let errorRed = Color(hex: "#E53935")
    static let audioPlayerFill = Color(hex: "#FEF3EC")
}
