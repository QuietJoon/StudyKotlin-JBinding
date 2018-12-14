fun initialize(ignoringListConfigPath: RealPath) {
    // Set IgnoringList
    val theIgnoringList = readIgnoringList(ignoringListConfigPath)
    val emptyTheTable = TheTable(emptyArray())
    emptyTheTable.setIgnoringList(theIgnoringList)
}