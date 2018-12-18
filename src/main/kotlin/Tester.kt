fun testWithTheTable(theTable: TheTable): Pair<String, Array<String>> {
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
    val resultList = mutableListOf<String>()
    for (anItemEntry in theTable.theItemTable) {
        if (!anItemEntry.value.isFilled) {
            count++
            val stringBuilder = StringBuilder()
            stringBuilder.append(anItemEntry.key.toString())
            stringBuilder.append(anItemEntry.value.toString())
            val theString = stringBuilder.toString()
            resultList.add(theString)
            print(theString)
        }
    }
    val resultArray = resultList.toTypedArray()

    if (count == 0 ) println("Have no different files in the ArchiveSets")
    val resultColor = when {
        count == 0 -> "Green"
        count > 0 -> "Red"
        else -> error("<testWithTheTable>: Can't be")
    }

    theTable.printSameItemTable(160, true, false)

    theTable.closeAllArchiveSets()

    return Pair(resultColor, resultArray)
}
