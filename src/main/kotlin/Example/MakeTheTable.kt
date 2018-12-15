import archive.*


fun main (args: Array<String>) {

    val theIgnoringListPath = "U:\\Kazuki\\AD\\IgnoringList.20181214.txt"
    initialize(theIgnoringListPath)

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
        val archiveSet = ArchiveSet(arrayOf(archivePath),idx,idx,ans.inArchive)
        archiveSetList.add(archiveSet)
    }

    var theTable = TheTable(archiveSetList.toTypedArray())

    println(theTable.archiveSetNum)
    println(theIgnoringList.ignoringList.size)
    println(theTable.theItemTable.size)
    println(theTable.theItemList.size)
    println(theTable.theArchiveSets[0].itemList.size)
    for ( anArchiveSet in theTable.theArchiveSets) {
        printItemList(anArchiveSet, anArchiveSet.getThisIDs())
    }
}
