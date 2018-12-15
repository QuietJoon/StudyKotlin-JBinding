import util.getFullName


class TheTable (
      val theArchiveSets: Array<ArchiveSet>
) {
    val theItemTable: ItemRecordTable = mutableMapOf()
    val theItemList: ItemTable = mutableMapOf()
    val archiveSetNum: Int

    init {
        archiveSetNum = theArchiveSets.size
    }

    fun setupTheTable() {
        // Initialize theItemTable and theItemList
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
    , val isArchive: Boolean? // null when exe is not sure
) {

    fun getFullName() = path.getFullName()

    fun generateItemKey() = ItemKey(dataCRC, dataSize, 1)
    fun generateItemKey(dupCount: Int) = ItemKey(dataCRC, dataSize, dupCount)
}

typealias ItemRecordTable = MutableMap<ItemKey,ItemRecord>
