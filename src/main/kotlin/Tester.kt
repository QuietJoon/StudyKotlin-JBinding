fun testWithTheTable(theTable: TheTable) {
    println("Number of ArchiveSet: ${theTable.archiveSetNum}")
    println("Size of TheIgnoringList: ${theIgnoringList.ignoringList.size}")
    println("Size of TheItemTable: ${theTable.theItemTable.size}")
    println("Size of TheItemList: ${theTable.theItemList.size}")
    println("Size of ItemList of ArchiveSet 0: ${theTable.theArchiveSets[0].itemList.size}")

    /*
    for (anArchiveSet in theTable.theArchiveSets)
        printItemList(anArchiveSet, anArchiveSet.getThisIDs())
        */

    for (anItemRecord in theTable.theItemTable) {
        print(anItemRecord.key.toString())
        println(anItemRecord.value.toString())
    }
}