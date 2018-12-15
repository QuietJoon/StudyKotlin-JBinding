import util.getFullName


class TheTable (
      val theArchiveSets: Array<ArchiveSet>
) {
    val theItemTable: ItemRecordTable = mutableMapOf()
    val theItemList: ItemTable = mutableMapOf()
    val archiveSetNum: Int

    init {
        archiveSetNum = theArchiveSets.size
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

    fun Array<ItemID?>.isFilled(): Boolean {
        this.forEach{ if(it==null) return false }
        return true
    }
}

data class ItemKey (
      val dataCRC: Int
    , val dataSize: DataSize
    , val dupCount: Int
) {
    override fun toString(): String {
        val stringBuilder = StringBuilder()
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

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
