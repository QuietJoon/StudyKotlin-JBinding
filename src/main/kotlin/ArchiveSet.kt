import archive.ArchiveAndStream
import net.sf.sevenzipjbinding.IInArchive
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem


class ArchiveSet (
      val realArchiveSetPaths: Array<RealPath>
    , val archiveSetID: ArchiveSetID
    , val rootArchiveSetID: ArchiveSetID
    , val ans: ArchiveAndStream
    , val itemID: ItemID
) {
    val itemList: ItemTable
    val subArchiveSetList: MutableList<ArchiveSet>

    init {
        itemList = mutableMapOf()
        subArchiveSetList = mutableListOf()

        // Initialize itemList

        val simpleArchive = ans.inArchive.simpleInterface
        for (sItem in simpleArchive.archiveItems) {
            if (!sItem.isFolder)
                addNewItem(sItem)
        }
    }

    fun addNewItem(sItem: ISimpleInArchiveItem) {
        val anItem = sItem.makeItemFromArchiveItem(
            realArchiveSetPaths
            , itemID
            , archiveSetID
        )
        if (theIgnoringList.match(anItem)) {
            println("Skip: ${anItem.path.last()}")
            return
        }
        var aKey = anItem.generateItemKey()
        while (true) {
            val queryItem = itemList[aKey]
            if (queryItem == null) {
                itemList[aKey] = anItem
                break
            // This condition never satisfied
            } else if (queryItem.equalsWithoutRealPath(anItem)) {
                println("[ERROR]<addNewItem>: Skip because completely same item")
                itemList[aKey] = anItem
                break
            } else {
                aKey = aKey.copy(dupCount = aKey.dupCount + 1)
            }
        }
    }

    fun getThisIDs(): Array<ItemIndices> {
        val aList = mutableListOf<ItemIndices>()
        for ( itemPair in itemList ) {
            val item = itemPair.value
            aList.add(Triple(item.parentArchiveSetID,item.idInArchive,rootArchiveSetID))
        }
        return aList.toTypedArray()
    }

    fun addSubArchiveSet() {
        error("Implement <addSubArchiveSet>")
    }

    fun getInArchive(archiveSetID: ArchiveSetID): IInArchive? {
        if (archiveSetID == this.archiveSetID)
            return ans.inArchive
        else {
            for (subArchiveSet in subArchiveSetList) {
                val result = subArchiveSet.getInArchive(archiveSetID)
                if (result != null) return result
            }
            return null
        }
    }

    fun getInArchive(): IInArchive {
        return ans.inArchive
    }
}

typealias ArchiveSetID = Int
typealias ItemIndices = Triple<ArchiveSetID,ItemIndex,ArchiveSetID>
typealias ItemTable = MutableMap<ItemKey,Item>
