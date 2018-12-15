import archive.*


fun main (args: Array<String>) {

    val theIgnoringListPath = "U:\\Kazuki\\AD\\IgnoringList.20181214.txt"
    initialize(theIgnoringListPath)

    /*
    val xArchivePaths: Array<RealPath> = arrayOf(
          "R:\\TestArchives\\XA.rar"
        , "R:\\TestArchives\\XB.rar"
        , "R:\\TestArchives\\XC.zip"
    )

    val xArchiveSetList = mutableListOf<ArchiveSet>()
    xArchivePaths.forEachIndexed { idx, archivePath ->
        val ans = openArchive(archivePath)
        val archiveSet = ArchiveSet(arrayOf(archivePath),idx,idx,ans.inArchive)
        xArchiveSetList.add(archiveSet)
    }

    var xTable = TheTable(xArchiveSetList.toTypedArray())

    println(xTable.archiveSetNum)
    println(xTable.theItemTable.size)
    println(xTable.theItemList.size)
    println(xTable.theArchiveSets[0].itemList.size)
    for ( anArchiveSet in xTable.theArchiveSets)
        printItemList(anArchiveSet, anArchiveSet.getThisIDs())

    for ( anItemRecord in xTable.theItemTable ) {
        print(anItemRecord.toPair().first.toString())
        println(anItemRecord.toPair().second.toString())
    }

    for (anKey in xTable.theItemTable.keys)
        println(anKey.toString())
    */

    val theArchivePaths: Array<RealPath> = arrayOf(
          "R:\\TestArchives\\ZA0.rar"
        , "R:\\TestArchives\\ZA1.rar"
        , "R:\\TestArchives\\ZA2.zip"
        , "R:\\TestArchives\\ZA3.zip"
    )

    val archiveSetList = mutableListOf<ArchiveSet>()
    theArchivePaths.forEachIndexed { idx, archivePath ->
        val ans = openArchive(archivePath)
        val archiveSet = ArchiveSet(arrayOf(archivePath),idx,idx,ans.inArchive)
        archiveSetList.add(archiveSet)
    }

    var theTable = TheTable(archiveSetList.toTypedArray())

    println(theIgnoringList.ignoringList.size)
    println(theTable.archiveSetNum)
    println(theTable.theItemTable.size)
    println(theTable.theItemList.size)
    println(theTable.theArchiveSets[0].itemList.size)
    for ( anArchiveSet in theTable.theArchiveSets)
        printItemList(anArchiveSet, anArchiveSet.getThisIDs())

    for ( anItemRecord in theTable.theItemTable ) {
        print(anItemRecord.toPair().first.toString())
        println(anItemRecord.toPair().second.toString())
    }

    for (anKey in theTable.theItemTable.keys)
        println(anKey.toString())
}
