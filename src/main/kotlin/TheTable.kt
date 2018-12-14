class TheTable (
    val theArchiveSets: Array<ArchiveSet>,
    var theItemTable: ItemRecordTable,
    var theItemList: ItemTable
) {
    companion object {
        var ignoringList: IgnoringList? = null
    }

    val archiveSetNum: Int

    init {
        archiveSetNum = theArchiveSets.size
    }

    fun setIgnoringList(newIgnoringList: IgnoringList) { ignoringList = newIgnoringList }
    fun getIgnoringList(): IgnoringList {
        if (ignoringList == null) {
            error("[ERROR]<getIgnoringList>: ignoringList is not initialized")
        } else {
            return ignoringList as IgnoringList
        }
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

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
typealias ItemTable = MutableMap<ItemKey,ItemRecord>
