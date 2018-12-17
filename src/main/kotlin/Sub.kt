import archive.openArchive

fun initialize(ignoringListConfigPath: RealPath) {
    // Set IgnoringList
    theIgnoringList = readIgnoringList(ignoringListConfigPath)
}

fun makeTheTable(theArchivePaths: Array<RealPath>, rootOutputDirectory: String): TheTable {
    val archiveSetList = mutableListOf<ArchiveSet>()
    theArchivePaths.forEachIndexed { idx, archivePath ->
        val ans = openArchive(archivePath)
        val archiveSet = ArchiveSet(arrayOf(archivePath),idx,idx,ans)
        archiveSetList.add(archiveSet)
    }

    return TheTable(archiveSetList.toTypedArray(), rootOutputDirectory)
}
