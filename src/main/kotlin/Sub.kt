fun initialize(ignoringListConfigPath: RealPath) {
    // Set IgnoringList
    theIgnoringList = readIgnoringList(ignoringListConfigPath)
}