import util.getFullName

class TheTable (
      val theArchiveSets: Array<ArchiveSet>
) {
    companion object {
        lateinit var ignoringList: IgnoringList
    }

    var theItemTable: ItemRecordTable = mutableMapOf()
    var theItemList: ItemTable = mutableMapOf()
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
    , val existance: Array<ItemID?>
    , val isArchive: Boolean
) {

    fun getFullName() = path.getFullName()

    fun generateItemKey() = ItemKey(dataCRC, dataSize, 1)
    fun generateItemKey(dupCount: Int) = ItemKey(dataCRC, dataSize, dupCount)
}

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
