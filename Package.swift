// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorNfcPlugin",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CapacitorNfcPlugin",
            targets: ["NFCPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "NFCPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/NFCPlugin"),
        .testTarget(
            name: "NFCPluginTests",
            dependencies: ["NFCPlugin"],
            path: "ios/Tests/NFCPluginTests")
    ]
)