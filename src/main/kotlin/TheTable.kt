import util.getFullName

class TheTable (
      var theArchiveSets: Array<ArchiveSet>
    , var theItemTable: ItemRecordTable
    , var theItemList: ItemTable
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
      val dataCRC: Int
    , val dataSize: DataSize
    , val dupCount: Int
)

class ItemRecord (
      val dataCRC: Int
    , val dataSize: DataSize
    , val modifiedDate: Date
    , val path: Path
    , val existance: Array<Item>
    , val isArchive: Boolean
) {

    fun getFullName() = path.getFullName()

    fun generateItemKey() = ItemKey(dataCRC, dataSize, 1)
    fun generateItemKey(dupCount: Int) = ItemKey(dataCRC, dataSize, dupCount)
}

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
typealias ItemTable = MutableMap<ItemKey,ItemRecord>
