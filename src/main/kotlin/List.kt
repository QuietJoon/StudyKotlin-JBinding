import archive.printItemListByIDs
import net.sf.sevenzipjbinding.IInArchive


fun printIgnoringList(ignoringList: IgnoringList) {

    println("   CRC    |   Size    |         Modified Date        | Filename")
    println("----------+-----------+------------------------------+---------")

    for (item in ignoringList.ignoringList) {
        println(
            String.format(
                " %08X | %9s | %28s | %s",
                item.itemCRC.datum,
                item.itemSize.datum,
                java.util.Date(item.itemModifiedDate.datum).toString(),
                item.itemName.datum
            )
        )
    }
}

fun printIgnoringListWithLevel(ignoringList: IgnoringList) {

    println("     CRC     |     Size     |           Modified Date         |  Filename")
    println("-------------+--------------+---------------------------------+----------")

    for (item in ignoringList.ignoringList) {
        println(
            String.format(
                " %s %08X | %s %9s | %s %28s | %s %s",
                item.itemCRC.level.toShortString(),
                item.itemCRC.datum,
                item.itemSize.level.toShortString(),
                item.itemSize.datum,
                item.itemModifiedDate.level.toShortString(),
                java.util.Date(item.itemModifiedDate.datum).toString(),
                item.itemName.level.toShortString(),
                item.itemName.datum
            )
        )
    }
}

fun getIDArrayWithoutIgnoringItem(inArchive: IInArchive, ignoringList: IgnoringList): IntArray {
    val simpleInArchive = inArchive.getSimpleInterface()
    var idList = mutableListOf<Int>()

    simpleInArchive.archiveItems.forEachIndexed { idx, sItem ->
        val item: Item = sItem.makeItemFromArchiveItem(emptyArray(),0,0)
        if (!ignoringList.match(item)) {
            idList.add(idx)
        }
    }

    return idList.toIntArray()
}

fun printItemList(archiveSet: ArchiveSet, itemIDs: Array<ItemIndices>) {
    itemIDs.sortWith(Comparator { a, b ->
        if (a.first == b.first)
            a.second - b.second
        else a.first - b.first
    }
    )

    var lastArchiveSetID: ArchiveSetID? = null
    var inArchive: IInArchive? = null
    var itemIndexList: MutableList<Int> = mutableListOf()
    for (idPair in itemIDs) {
        val theArchiveSetID = idPair.first
        val theItemIndex = idPair.second
        if (lastArchiveSetID != theArchiveSetID) {
            if (inArchive != null)
                printItemListByIDs(inArchive, itemIndexList.toIntArray())
            lastArchiveSetID = theArchiveSetID
            inArchive = archiveSet.getInArchive(theArchiveSetID)
            itemIndexList = mutableListOf()
        }
        if (inArchive == null)
            error("[ERROR]<printItemList>: inArchive($theArchiveSetID) found")
        itemIndexList.add(theItemIndex)
    }
    if (inArchive != null)
        printItemListByIDs(inArchive, itemIndexList.toIntArray())
}
