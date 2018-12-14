import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import util.getFullName


class TheTable (
    val theArchiveSets: Array<ArchiveSet>,
    var theItemTable: ItemRecordTable,
    var theItemList: ItemTable
) {
    companion object {
        var ignoringList: ItemRecordTable? = null
    }

    val archiveSetNum: Int

    init {
        archiveSetNum = theArchiveSets.size
    }

    fun setIgnoringList(records: ItemRecordTable) { ignoringList = records }
    fun getIgnoringList(): ItemRecordTable? = ignoringList

    fun registerItem () {

    }
}

data class ItemKey (
    val dataCRC: Int,
    val dataSize: DataSize
)

class ItemRecord (
    val dataCRC: Int,
    val dataSize: DataSize,
    val modifiedDate: Date,
    val name: Name,
    val existance: Array<Item>,
    val isArchive: Boolean
) {
    fun generateItemKey() = ItemKey(dataCRC, dataSize)
}

class Item (
    val dataCRC: Int,
    val dataSize: DataSize,
    val modifiedDate: Date,
    val path: JointPath,
    val parentID: ItemID?,
    val idInArchive: ItemID,
    val parentArchiveSetID: ArchiveSetID
) {
    val id: ItemID

    companion object {
        var serialCount = 0
    }

    init {
        id = serialCount
        serialCount += 1
    }

    fun getFullName() = path.last().getFullName()
}

fun ISimpleInArchiveItem.makeItemFromArchiveItem(parentPath: JointPath, parentID: ItemID, idInArchive: ItemID, parentArchiveSetID: ArchiveSetID): Item {

    val newPath = parentPath.toMutableList()
    newPath.add(this.path)

    return Item (
          dataCRC = this.crc
        , dataSize = this.size
        , modifiedDate = this.lastWriteTime.time
        , path = newPath.toList().toTypedArray()
        , parentID = parentID
        , idInArchive = idInArchive
        , parentArchiveSetID = parentArchiveSetID
    )
}

typealias ItemID = Int
typealias Date = Long
typealias Name = String
typealias DataSize = Long
typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
typealias ItemTable = MutableMap<ItemKey,ItemRecord>
