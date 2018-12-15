import net.sf.sevenzipjbinding.IInArchive
import util.getFullName
import java.util.*


class TheTable (
      val theArchiveSets: Array<ArchiveSet>
) {
    val theItemTable: ItemRecordTable = sortedMapOf()
    val theItemList: ItemTable = mutableMapOf()
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
            val ids = anArchiveSet.getThisIDs()
            for ( idPair in ids) {
                registerAnItemRecord(anArchiveSet,idPair)
            }
        }
    }

    fun registerAnItemRecord(anArchiveSet: ArchiveSet, idPair: ItemIndices) {
        val anItem = anArchiveSet.inArchive.simpleInterface
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
        } else if (queryItemRecord.existance[idPair.third] == null) {
            val newExistance = queryItemRecord.existance
            newExistance[idPair.third] = idPair.first
            theItemTable[aKey]!!.existance = newExistance
        } else {
            println("[WARN]<registerAnItemRecord>: add again ${anItem.path.last()}")
        }
        if (theItemTable[aKey]!!.existance.isFilled())
            theItemTable[aKey]!!.isFilled = true

        /*
        aKey = anItem.generateItemKey()
        while (true) {
            val queryItem = theItemList[aKey]
            if (queryItem != anItem) {
                aKey = aKey.copy(dupCount = aKey.dupCount + 1)
                theItemList[aKey] = anItem
                break
            }
        }
        */
    }

    fun registerAnItemRecord(anArchiveSet: ArchiveSet, idPair: ItemIndices, beforeExistance: Array<ItemID?>) {
        val anItem = anArchiveSet.inArchive.simpleInterface
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

        /*
        aKey = anItem.generateItemKey()
        while (true) {
            val queryItem = theItemList[aKey]
            if (queryItem != anItem) {
                aKey = aKey.copy(dupCount = aKey.dupCount + 1)
                theItemList[aKey] = anItem
                break
            }
        }
        */
    }

    fun mergeExistance(a:Array<ItemID?>, b:Array<ItemID?>): Array<ItemID?> {
        val new = arrayOfNulls<ItemID?>(a.size)
        for (i in 0.until(a.size)) {
            if (a[i] != null) {
                new[i] = a[i]
            } else if (b[i] != null) {
                new[i] = b[i]
            }
        }
        return new
    }

    fun Array<ItemID?>.isFilled(): Boolean {
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
                break
            }
        }
        error("[ERROR]<getInArchive>: Couldn't find InArchive($archiveSetID)")
    }

    fun getInArchiveSub(archiveSetID: ArchiveSetID, anArchiveSet: ArchiveSet): IInArchive? {
        if (anArchiveSet.archiveSetID == archiveSetID)
            return anArchiveSet.inArchive
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
    , val path: Path
    , var existance: Array<ItemID?>
    , var isFilled: Boolean
    , val isArchive: Boolean? // null when exe is not sure
) {
    fun getFullName() = path.getFullName()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(if (isFilled) "O " else "X ")
        stringBuilder.append(if (isArchive==null) "? " else if (isArchive) "A " else "F ")
        for(i in existance)
            stringBuilder.append(if (i==null) "  -" else String.format("%3d",i))
        stringBuilder.append("  ")
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
