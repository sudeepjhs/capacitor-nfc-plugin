import Foundation
import Capacitor
import CoreNFC

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(NFCPlugin)
public class NFCPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "NFCPlugin"
    public let jsName = "NFC"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isAvailable", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "scanTag", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "writeTag", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = NFC()
    private var nfcSession: NFCNDEFReaderSession?

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }

    @objc func isAvailable(_ call: CAPPluginCall) {
        let available = NFCNDEFReaderSession.readingAvailable
        call.resolve([
            "available": available
        ])
    }

    @objc func scanTag(_ call: CAPPluginCall) {
        guard NFCNDEFReaderSession.readingAvailable else {
            call.reject("NFC is not available on this device")
            return
        }

        nfcSession = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: false)
        nfcSession?.alertMessage = "Hold your iPhone near an NFC tag to scan it."
        nfcSession?.begin()
        call.resolve()
    }

    @objc func writeTag(_ call: CAPPluginCall) {
        let data = call.getString("data") ?? ""
        guard !data.isEmpty else {
            call.reject("Data is required")
            return
        }

        guard NFCNDEFReaderSession.readingAvailable else {
            call.reject("NFC is not available on this device")
            return
        }

        implementation.setDataToWrite(data)
        nfcSession = NFCNDEFReaderSession(delegate: self, queue: nil, invalidateAfterFirstRead: false)
        nfcSession?.alertMessage = "Hold your iPhone near an NFC tag to write to it."
        nfcSession?.begin()
        call.resolve()
    }
}

extension NFCPlugin: NFCNDEFReaderSessionDelegate {
    public func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        guard let message = messages.first, let record = message.records.first else {
            session.invalidate(errorMessage: "No NDEF message found")
            return
        }

        if let text = record.wellKnownTypeTextPayload() {
            let tagId = "iOS_NFC_TAG" // iOS doesn't provide tag ID directly
            let result: [String: Any] = [
                "tagId": tagId,
                "data": text.0
            ]
            notifyListeners("nfcTagScanned", data: result)
        }
    }

    public func readerSession(_ session: NFCNDEFReaderSession, didDetect tags: [NFCNDEFTag]) {
        if tags.count > 0 {
            session.connect(to: tags.first!) { error in
                if error != nil {
                    session.invalidate(errorMessage: "Connection error")
                    return
                }

                tags.first!.queryNDEFStatus { status, capacity, error in
                    if error != nil {
                        session.invalidate(errorMessage: "Query error")
                        return
                    }

                    if status == .readWrite {
                        if let dataToWrite = self.implementation.getDataToWrite() {
                            let payload = NFCNDEFPayload.wellKnownTypeTextPayload(string: dataToWrite, locale: Locale(identifier: "en"))!
                            let message = NFCNDEFMessage(records: [payload])

                            tags.first!.writeNDEF(message) { error in
                                if error != nil {
                                    session.invalidate(errorMessage: "Write error")
                                } else {
                                    session.alertMessage = "Data written successfully"
                                    session.invalidate()
                                }
                            }
                        } else {
                            // Reading
                            tags.first!.readNDEF { message, error in
                                if let message = message, let record = message.records.first, let text = record.wellKnownTypeTextPayload() {
                                    let tagId = "iOS_NFC_TAG"
                                    let result: [String: Any] = [
                                        "tagId": tagId,
                                        "data": text.0
                                    ]
                                    self.notifyListeners("nfcTagScanned", data: result)
                                    session.alertMessage = "Tag scanned successfully"
                                    session.invalidate()
                                } else {
                                    session.invalidate(errorMessage: "Read error")
                                }
                            }
                        }
                    } else {
                        session.invalidate(errorMessage: "Tag is not writable")
                    }
                }
            }
        }
    }

    public func readerSessionDidBecomeActive(_ session: NFCNDEFReaderSession) {
        // Session started
    }

    public func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        // Session invalidated
    }
}
