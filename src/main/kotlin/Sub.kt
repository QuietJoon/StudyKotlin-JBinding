fun initialize(ignoringListConfigPath: RealPath): IgnoringList {
    // Set IgnoringList
    val theIgnoringList = readIgnoringList(ignoringListConfigPath)
    val emptyTheTable = TheTable(emptyArray())
    emptyTheTable.setIgnoringList(theIgnoringList)

    return theIgnoringList
}