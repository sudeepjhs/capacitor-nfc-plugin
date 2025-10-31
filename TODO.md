# TODO: Implement Full NFC Plugin for Capacitor

## Steps to Complete

- [x] Update src/definitions.ts to add NFC methods: isAvailable(), scanTag(), writeTag(options)
- [x] Update src/web.ts with stubs for web implementation
- [x] Implement Android native code in NFC.java for NFC operations (using NfcAdapter)
- [x] Implement Android native code in NFCPlugin.java for plugin methods
- [x] Add NFC permissions to android/src/main/AndroidManifest.xml
- [x] Implement iOS native code in NFC.swift using CoreNFC
- [x] Implement iOS native code in NFCPlugin.swift for plugin methods
- [x] Add NFC permissions to iOS plist (create if needed)
- [ ] Build the plugin using pnpm run build
- [ ] Test on Android and iOS devices
- [ ] Publish to npm for Outsystems integration
