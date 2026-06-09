//
//  String+Extension.swift
//  iosApp
//
//  Created by Amarjith B on 09/06/26.
//


//
//  Strings.swift
//  InstaResolv
//
//  Created by Amarjith B on 26/02/26.
//
import SwiftUI

extension String {
    func localizedString() -> String {
        LocalizedStringKey(self).stringValue()
    }
    
    func localizedStringInFormat(arguments: [CVarArg]) -> String {
        let bundle = Bundle.localizedBundle(for: LocalizationService.shared.language)
        let localizedString = String(format: NSLocalizedString(self, bundle: bundle, comment: ""), arguments: arguments)
        return localizedString
    }
}

extension LocalizedStringKey {
    var stringKey: String {
        let description = "\(self)"

        let components = description.components(separatedBy: "key: \"")
            .map { $0.components(separatedBy: "\",") }

        return components[1][0]
    }
}

extension String {
    static func localizedString(for key: String,
                                locale: Locale = .current) -> String {
        let bundle = Bundle.localizedBundle(for: LocalizationService.shared.language)
        return NSLocalizedString(key, bundle: bundle, comment: "")
    }
}

extension LocalizedStringKey {
    func stringValue(locale: Locale = .current) -> String {
        return .localizedString(for: self.stringKey, locale: locale)
    }
}


extension String {
    var successToast: AppToast {
        AppToast.success(title: "common.success".localizedString(), self)
    }
    var infoToast: AppToast {
        AppToast.info(title: "common.info".localizedString(), self)
    }
    var errorToast: AppToast {
        AppToast.error(title: "common.error".localizedString(), self)
    }
    var warningToast: AppToast {
        AppToast.warning(title: "common.warning".localizedString(), self)
    }
}
//
//extension String {
//    func viewRetry(isDarkMode: Bool = false, action: @escaping () -> Void) -> ErrorRetryView {
//        ErrorRetryView(retry: { action() },
//                       title: nil,
//                       message: self,
//                       isDarkMode: isDarkMode)
//    }
//}


extension String {
    func enterPlaceHolder(isCapital: Bool = false) -> String {
        let enterText = isCapital ? "ENTER".localizedString() : "Enter".localizedString()
        return isCapital ? "\(enterText) \(self.uppercased())" : "\(enterText) \(self)"
    }
}

extension String {
    func fieldLimit(limit: Int) -> String {
        if count > limit {
                       let newString = String(self.prefix(limit))
            return newString
        }
        return self
    }
}

extension String {
    var url: URL? {
        let encoded = self.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed)
        return URL(string: encoded ?? self)
    }
    
    var toInt: Int? {
        Int(self)
    }
}

extension String {
    func convertDateFormater(dateFormat: String, inputFormat: String, local: Locale) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = inputFormat
        guard let date = dateFormatter.date(from: self) else { return "" }
        dateFormatter.dateFormat = dateFormat
        dateFormatter.locale = local
        return dateFormatter.string(from: date)
        
    }
    
    func toDateFormat(_ format: String? = nil) -> String? {
        let formatter = DateFormatter()
        formatter.dateFormat = Constants.DateFormat.REPO_DATE
        if let date = formatter.date(from: self) {
            if let format = format {
                formatter.dateFormat = format
            } else {
                formatter.dateStyle = .medium
            }
            return formatter.string(from: date)
        }
        return nil
    }
    
    func convertTImeFormat() -> String {
         let dateFormatter = DateFormatter()
         dateFormatter.dateFormat = "HH:mm:ss"
         let dateFromStr = dateFormatter.date(from: self)!
         dateFormatter.dateFormat = "h:mm a"
         let timeFromDate = dateFormatter.string(from: dateFromStr)
         return timeFromDate
     }
    
    func repoDate(
        inputFormat: String = Constants.DateFormat.REPO_DATE,
        local: Locale
    ) -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.locale = local                // apply locale
        dateFormatter.calendar = Calendar(identifier: .gregorian)  // important!
        dateFormatter.dateFormat = inputFormat
        return dateFormatter.date(from: self)
    }

    
    func repoTimeDate() -> Date? {
        let dateFormatter = DateFormatter()
        dateFormatter.timeStyle = .short
        return dateFormatter.date(from: self)
    }
}

extension String {
    var selectPlaceholder: String {
        "pick".localizedString() + Constants.SPACE + self
    }
}

extension String {
    func modifyTogroupCode() -> String {
        if !(isEmpty) && (count > Constants.Number.Limit.GROUPCODEPARTONE) {
            let new = dropLast()
            return String(new)
        }

        let ACCEPTABLE_CHARACTERS = "ABCDEFGHIJKLKMNOPQRSTUVWXYZ0123456789"
        let cs = NSCharacterSet(charactersIn: ACCEPTABLE_CHARACTERS).inverted
        let filtered = components(separatedBy: cs).joined(separator: "")
        if self != filtered {
            let new = self.dropLast()
            return String(new)
        }

        return self
    }
}

extension String {
    var localizable: String {
        NSLocalizedString(self, comment: "")
    }
}

//extension String {
//    func validatedText(validationType: ValidatorType) throws -> String {
//        let validator = VaildatorFactory.validatorFor(type: validationType)
//        return try validator.validated(self) ?? ""
//    }
//
//    func isEmptyOrWhitespace() -> Bool {
//        return self.trimmingCharacters(in: .whitespaces).isEmpty
//    }
//}
//
//extension String {
//    func viewRetry(isDarkMode: Bool = false, action: @escaping () -> Void) -> ErrorRetryView {
//        ErrorRetryView(retry: { action() },
//                       title: nil,
//                       message: self,
//                       isDarkMode: isDarkMode)
//    }
//    
//    func isValidEmail() -> Bool {
//        let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
//        return NSPredicate(format:"SELF MATCHES %@", emailRegEx).evaluate(with: self)
//    }
//}

extension String {
    
    func timeAgoString(inputFormat: String = "yyyy-MM-dd HH:mm:ss") -> String {
        
        let formatter = DateFormatter()
        formatter.dateFormat = inputFormat
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(abbreviation: "UTC") // API time
        
        guard let date = formatter.date(from: self) else {
            return ""
        }
        
        let relativeFormatter = RelativeDateTimeFormatter()
        relativeFormatter.unitsStyle = .full
        
        return relativeFormatter.localizedString(for: date, relativeTo: Date())
    }
    
    func utcToLocal(
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        outputFormat: String = "yyyy-MM-dd HH:mm:ss"
    ) -> String? {
        
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.dateFormat = inputFormat
        formatter.timeZone = TimeZone(secondsFromGMT: 0) // UTC
        
        guard let date = formatter.date(from: self) else { return nil }
        
        formatter.timeZone = TimeZone.current // Local
        formatter.dateFormat = outputFormat
        
        return formatter.string(from: date)
    }
}

extension String {
    
    func utcToDate(
        inputFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    ) -> Date? {
        
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.dateFormat = inputFormat
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        
        return formatter.date(from: self)
    }
}
