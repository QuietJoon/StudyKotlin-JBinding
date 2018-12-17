import archive.ArchiveAndStream
import net.sf.sevenzipjbinding.IInArchive
import util.getFullName
import java.util.*


class TheTable (
      val theArchiveSets: Array<ArchiveSet>
    , val rootOutputDirectory: RealPath
) {
    val theItemTable: ItemRecordTable = sortedMapOf()
    val theItemList: ItemTable = mutableMapOf()
    val theArchiveList: ArchiveSetList = mutableMapOf()
    val archiveSetNum: Int
    val tableInstance: Int

    companion object {
        var tableInstanceSerial = 1
    }

    init {
        archiveSetNum = theArchiveSets.size
        tableInstance = tableInstanceSerial
        tableInstanceSerial++
        for( anArchiveSet in theArchiveSets ) {
            registerAnArchiveSet(anArchiveSet)
            for ( idx in anArchiveSet.itemList.keys) {
                registerAnItemRecord(anArchiveSet,idx)
            }
        }
    }

    fun registerAnArchiveSet(anArchiveSet: ArchiveSet) {
        theArchiveList[anArchiveSet.archiveSetID] = anArchiveSet
    }

    fun registerAnItemRecord(anArchiveSet: ArchiveSet, idx: ItemKey) {
        /*
        FIX:
        Re-generate an Item from ItemIndices
        However, the Item may be(or should be) in ArchiveSet already
        Also, anItem contains ItemIndices info
        Therefore, we just need to give index of anItem in ArchiveSet's itemList
        This should be concerned with extracting file function.
         */
        val anItem: Item = anArchiveSet.itemList[idx]!!
        if (theIgnoringList.match(anItem)) {
            println("Skip: ${anItem.path.last()}")
            return
        }

        val idPair = Triple(anItem.parentArchiveSetID,anItem.id,anArchiveSet.rootArchiveSetID)
        var aKey = anItem.generateItemKey()
        val queryItemRecord: ItemRecord? = theItemTable[aKey]
        if (queryItemRecord == null) {
            val anItemRecord = anItem.makeItemRecordFromItem(archiveSetNum,idPair.third,idPair.first)
            theItemTable[aKey] = anItemRecord
        } else if (queryItemRecord.existance[idPair.third] == null) {
            val newExistance = queryItemRecord.existance
            newExistance[idPair.third] = Pair(idPair.first,idPair.second)
            theItemTable[aKey]!!.existance = newExistance
        } else {
            println("[WARN]<registerAnItemRecord>: add again ${anItem.path.last()}")
        }
        if (theItemTable[aKey]!!.existance.isFilled())
            theItemTable[aKey]!!.isFilled = true

        aKey = anItem.generateItemKey()
        var count = 1
        while (true) {
            val queryItem = theItemList[aKey]
            if (queryItem != anItem) {
                aKey = aKey.copy(dupCount = count)
                theItemList[aKey] = anItem
                break
            } else {
                count++
            }
        }
    }

    fun registerAnItemRecord(anArchiveSet: ArchiveSet, idPair: ItemIndices, beforeExistance: ExistanceBoard) {
        val anItem = anArchiveSet.getInArchive().simpleInterface
            .getArchiveItem(idPair.second).makeItemFromArchiveItem(
                anArchiveSet.realArchiveSetPaths
                , 0
                , idPair.second
                , idPair.first
            )
        if (theIgnoringList.match(anItem)) {
            println("Skip: ${anItem.path.last()}")
            return
        }

        var aKey = anItem.generateItemKey()
        val queryItemRecord: ItemRecord? = theItemTable[aKey]
        if (queryItemRecord == null) {
            val anItemRecord = anItem.makeItemRecordFromItem(archiveSetNum,idPair.third,idPair.first)
            theItemTable[aKey] = anItemRecord
        } else {
            val newExistance = mergeExistance(queryItemRecord.existance,beforeExistance)
            theItemTable[aKey]!!.existance = newExistance
        }
        if (theItemTable[aKey]!!.existance.isFilled())
            theItemTable[aKey]!!.isFilled = true

        aKey = anItem.generateItemKey()
        var count = 1
        while (true) {
            val queryItem = theItemList[aKey]
            if (queryItem != anItem) {
                aKey = aKey.copy(dupCount = count)
                theItemList[aKey] = anItem
                break
            } else {
                count++
            }
        }
    }

    fun mergeExistance(a:ExistanceBoard, b:ExistanceBoard): ExistanceBoard {
        val new = arrayOfNulls<ExistanceMark>(a.size)
        for (i in 0.until(a.size)) {
            if (a[i] != null) {
                new[i] = a[i]
            } else if (b[i] != null) {
                new[i] = b[i]
            }
        }
        return new
    }

    fun ExistanceBoard.isFilled(): Boolean {
        this.forEach{ if(it==null) return false }
        return true
    }

    fun getFirstItemKey(): ItemKey? {
        theItemTable.forEach {
            if (!it.value.isFilled)
                if (it.key.isArchive != false) return null else it.key
        }
        return null
    }

    fun getInArchive(archiveSetID: ArchiveSetID): IInArchive {
        for ( anArchiveSet in theArchiveSets) {
            val result = getInArchiveSub(archiveSetID, anArchiveSet)
            if ( result != null ) {
                return result
            }
        }
        error("[ERROR]<getInArchive>: Couldn't find InArchive($archiveSetID)")
    }

    fun getInArchiveSub(archiveSetID: ArchiveSetID, anArchiveSet: ArchiveSet): IInArchive? {
        if (anArchiveSet.archiveSetID == archiveSetID)
            return anArchiveSet.getInArchive()
        for ( subArchiveSet in theArchiveSets ) {
            val result = getInArchiveSub(archiveSetID, subArchiveSet)
            if (result != null) return result
        }
        return null
    }
}

data class ItemKey (
      val isArchive: Boolean?
    , val dataCRC: Int
    , val dataSize: DataSize
    , val dupCount: Int
) : Comparable<ItemKey> {
    companion object {
        val comparator = compareBy(ItemKey::dataCRC, ItemKey::dataCRC, ItemKey::dupCount)
    }
    override fun compareTo(other: ItemKey): Int =
        if (this.isArchive == other.isArchive) {
            comparator.compare(this,other)
        }
        else when (this.isArchive) {
            true -> -1
            false -> 1
            null -> if (other.isArchive!!) 1 else -1
        }
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(if (isArchive==null) "? " else if (isArchive) "A " else "F ")
        stringBuilder.append(String.format("%08X", this.dataCRC))
        stringBuilder.append("  ")
        stringBuilder.append(String.format("%8d", this.dataSize))
        stringBuilder.append("  ")
        stringBuilder.append(String.format("%2d", this.dupCount))
        stringBuilder.append("  ")
        return stringBuilder.toString()
    }
}

data class ItemRecord (
      val dataCRC: Int
    , val dataSize: DataSize
    , val modifiedDate: Date
    , val path: RelativePath
    , var existance: ExistanceBoard
    , var isFilled: Boolean
    , val isArchive: Boolean? // null when exe is not sure
) {
    fun getFullName() = path.getFullName()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(if (isFilled) "O " else "X ")
        stringBuilder.append(if (isArchive==null) "? " else if (isArchive) "A " else "F ")
        for(i in existance)
            stringBuilder.append(if (i==null) "    -     " else String.format(" %3d-%-5d",i.first,i.second))
        stringBuilder.append(" | ")
        stringBuilder.append(String.format("%08X", this.dataCRC))
        stringBuilder.append("  ")
        stringBuilder.append(String.format("%8d", this.dataSize))
        stringBuilder.append("  ")
        stringBuilder.append(java.util.Date(this.modifiedDate).toString())
        stringBuilder.append("  ")
        stringBuilder.append(path)
        return stringBuilder.toString()
    }
}

typealias ItemRecordTable = SortedMap<ItemKey, ItemRecord>
typealias ItemTable = MutableMap<ItemKey,Item>
typealias ArchiveSetList = MutableMap<Int,ArchiveSet>
typealias ExistanceMark = Pair<ArchiveSetID,ItemID>
typealias ExistanceBoard = Array<ExistanceMark?>
