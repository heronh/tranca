// swift-tools-version: 5.9

import PackageDescription

let package = Package(
    name: "TrancaScoreCore",
    products: [
        .library(name: "TrancaScoreCore", targets: ["TrancaScoreCore"])
    ],
    targets: [
        .target(
            name: "TrancaScoreCore",
            path: "TrancaScore/App",
            sources: ["Game.swift"]
        ),
        .testTarget(
            name: "TrancaScoreCoreTests",
            dependencies: ["TrancaScoreCore"],
            path: "TrancaScore/Tests"
        )
    ]
)
