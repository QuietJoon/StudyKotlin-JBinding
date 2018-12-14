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

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
typealias ItemTable = MutableMap<ItemKey,ItemRecord>
