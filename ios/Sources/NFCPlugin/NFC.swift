import Foundation

@objc public class NFC: NSObject {
    private var dataToWrite: String?

    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }

    @objc public func setDataToWrite(_ data: String) {
        dataToWrite = data
    }

    @objc public func getDataToWrite() -> String? {
        return dataToWrite
    }
}
