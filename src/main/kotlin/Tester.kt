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

    for (anItemEntry in theTable.theItemTable) {
        print(anItemEntry.key.toString())
        println(anItemEntry.value.toString())
    }

    println("Difference only")
    var count = 0
    for (anItemEntry in theTable.theItemTable) {
        if (!anItemEntry.value.isFilled) {
            print(anItemEntry.key.toString())
            println(anItemEntry.value.toString())
            count++
        }
    }
    if (count == 0 ) println("Have no different files in the ArchiveSets")

    theTable.printSameItemTable(160, true, false)

    theTable.closeAllArchiveSets()
}
