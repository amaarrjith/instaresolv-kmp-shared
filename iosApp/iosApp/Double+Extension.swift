//
//  Double+Extension.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


import Foundation
import UIKit

extension Double {
    
    func ratiodWidth(width: CGFloat = 375) -> CGFloat {
        (CGFloat(self)/width) * UIScreen.main.bounds.width
    }
    
    func ratiodHeight(height: CGFloat = 838) -> CGFloat {
        (CGFloat(self)/height) * UIScreen.main.bounds.height
    }
    
    func rounded(places: Int = 0) -> String {
        String(format: "%.\(places)f", self)
    }
    
    func formatted() -> String {
        let formatter = NumberFormatter()
        formatter.numberStyle = .decimal
        
        return formatter.string(from: NSNumber(value: self)) ?? ""
    }
    
    func toInt() -> Int {
        Int(self)
    }
    
    func futureReminderTimeStamp() -> Double {
        let secondsInADay: TimeInterval = 86400
        return self - (secondsInADay)
    }
}
