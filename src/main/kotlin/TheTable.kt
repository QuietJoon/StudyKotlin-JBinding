import net.sf.sevenzipjbinding.IInArchive
import java.util.*
import java.io.File

import util.*

class TheTable (
      val theArchiveSets: Array<ArchiveSet>
) {
    var rootOutputDirectory: RealPath
    val theItemTable: ItemRecordTable = sortedMapOf()
    val theItemList: ItemList = mutableMapOf()
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
        rootOutputDirectory = theDebugDirectory + directoryDelimiter + tableInstanceSerial
    }

    constructor( theArchiveSets: Array<ArchiveSet>, rootOutputDirectory: RealPath ): this(theArchiveSets) {
        this.rootOutputDirectory = rootOutputDirectory + directoryDelimiter + tableInstanceSerial
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

        theItemList[anItem.id] = anItem
    }

    fun registerAnItemRecord(anArchiveSet: ArchiveSet, idx: ItemKey, beforeExistance: ExistanceBoard) {
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
        } else {
            val newExistance = mergeExistance(queryItemRecord.existance,beforeExistance)
            theItemTable[aKey]!!.existance = newExistance
        }
        if (theItemTable[aKey]!!.existance.isFilled())
            theItemTable[aKey]!!.isFilled = true

        theItemList[anItem.id] = anItem
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

    fun modifyKeyOfTheItemTable(oldKey: ItemKey, newKey: ItemKey) {
        val queriedValue = theItemTable[oldKey]
        println(theItemTable)
        if (queriedValue == null) {
            error("[ERROR]<modifyKey>: No such ItemRecord with $oldKey")
        } else {
            theItemTable.remove(oldKey)
            theItemTable.put(newKey,queriedValue)
        }
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

    fun printSameItemTable(len: Int, fullNameOnly: Boolean, relativePathOnly: Boolean) {
        for ( itemEntry in theItemTable ) {
            if (itemEntry.value.isFilled) {
                println(itemEntry.key)
                itemEntry.value.existance.forEachIndexed { anArchiveSetID, itemIdxs ->
                    val theItem = theItemList[itemIdxs!!.second]
                    val thePath = if (fullNameOnly) theItem!!.path.last().getFullName()
                                    else if (relativePathOnly) theItem!!.path.last()
                                    else theItem!!.path.joinToString(separator="|")
                    val regulatedPath = thePath.regulating(len)
                    print(regulatedPath+" | ")
                }
                println()
            }
        }
    }

    fun runOnce(): Boolean {
        val theKey = getFirstItemKey()
        if (theKey != null) {
            val theItemRecord = theItemTable[theKey]
            // When isArchive == null, check the file is archive or not
            // How to identify which ArchiveSet is used?
            //Extract(rootOutputDirectory+theItemRecord.,).extractSomething()
            return false
        }
        else return true
    }

    fun run() {
        while (true) {
            if (runOnce()) break
        }
    }

    fun closeAllArchiveSets() {
        for ( anArchive in theArchiveList ) {
            anArchive.value.ans.close()
        }
    }

    fun removeAllArchiveSets() {
        File(rootOutputDirectory).deleteRecursively()
    }

    fun prepareWorkingDirectory() {
        val theDirectory = File(rootOutputDirectory)
        if ( !theDirectory.exists() ) {
            println("<testWithTheTable>: Does not exist")

            File(rootOutputDirectory).mkdirs()
            if ( !theDirectory.mkdirs() ) {
                println("[ERROR]<testWithTheTable>: Fail to make directory")
            } else {
                println("<testWithTheTable>: Seems to be made")
            }
            if (!theDirectory.exists()) {
                error("[ERROR]<testWithTheTable>: Can't be")
            }
        }
    }
}

data class ItemKey (
      val isArchive: Boolean?
    , val dataCRC: Int
    , val dataSize: DataSize
    , val dupCount: Int
) : Comparable<ItemKey> {
    companion object {
        val comparatorKey =
            compareByDescending(ItemKey::dataSize)
                .thenBy(ItemKey::dataCRC)
                .thenBy(ItemKey::dupCount)
    }
    override fun compareTo(other: ItemKey): Int =
        if (this.isArchive == other.isArchive) {
            comparatorKey.compare(this,other)
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
        stringBuilder.append(String.format("%10d", this.dataSize))
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
    , var isArchive: Boolean? // null when exe is not sure
    , var isExtracted: Boolean
) {
    fun getFullName() = path.getFullName()

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append(if (isFilled) "O " else "X ")
        stringBuilder.append(if (isExtracted) "O " else if (isArchive == false) "  " else "- ")
        stringBuilder.append(if (isArchive==null) "? " else if (isArchive!!) "A " else "F ")
        for(i in existance)
            stringBuilder.append(if (i==null) "    -     " else String.format(" %3d-%-5d",i.first,i.second))
        stringBuilder.append(" | ")
        stringBuilder.append(String.format("%08X", this.dataCRC))
        stringBuilder.append("  ")
        stringBuilder.append(String.format("%10d", this.dataSize))
        stringBuilder.append("  ")
        stringBuilder.append(this.modifiedDate.dateFormatter())
        stringBuilder.append("  ")
        stringBuilder.append(path)
        return stringBuilder.toString()
    }
}

typealias ItemRecordTable = SortedMap<ItemKey, ItemRecord>
typealias ItemList = MutableMap<ItemID,Item>
//typealias ItemTable = MutableMap<ItemKey,Item>
typealias ArchiveSetList = MutableMap<Int,ArchiveSet>
typealias ExistanceMark = Pair<ArchiveSetID,ItemID>
typealias ExistanceBoard = Array<ExistanceMark?>
