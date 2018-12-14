import archive.*


fun main (args: Array<String>) {

    val theArchivePaths: Array<RealPath> = arrayOf(
          "R:\\TestArchives\\Source.zip"
        , "R:\\TestArchives\\SourceMultiRAR4.part1.rar"
        , "R:\\TestArchives\\SourceMultiRAR5.part1.rar"
        , "R:\\TestArchives\\SourceRAR4.rar"
        , "R:\\TestArchives\\SourceRAR5.rar"
    )

    val archiveSetList = mutableListOf<ArchiveSet>()
    theArchivePaths.forEachIndexed() { idx, archivePath ->
        val ans = openArchive(archivePath)
        val archiveSet = ArchiveSet(arrayOf(archivePath),idx+1,rootArchiveSetID,ans.inArchive,mutableMapOf(), emptyList())
        archiveSetList.add(archiveSet)
    }

    val theIgnoringListPath = "U:\\Kazuki\\AD\\IgnoringList.20181214.txt"
    val theIgnoringList = readIgnoringList(theIgnoringListPath)
    printIgnoringListWithLevel(theIgnoringList)

    for ( archiveSet in archiveSetList) {
        val notIgnoringItemIDArray = getIDArrayWithoutIgnoringItem(archiveSet.inArchive,theIgnoringList)
        printItemListByIDs(archiveSet.inArchive, notIgnoringItemIDArray)
    }
}
