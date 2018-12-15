import net.sf.sevenzipjbinding.IInArchive

class ArchiveSet (
      val realArchiveSetPaths: Array<RealPath>
    , val archiveSetID: ArchiveSetID
    , val rootArchiveSetID: ArchiveSetID
    , val inArchive: IInArchive
) {
    val itemList: ItemTable
    val subArchiveSetList: MutableList<ArchiveSet>

    init {
        itemList = mutableMapOf()
        subArchiveSetList = mutableListOf()
    }

    fun addNewItem(item: Item) {
        itemList.put(item.generateItemKey(),item)
    }
}

typealias ArchiveSetID = Int
typealias ItemTable = MutableMap<ItemKey,Item>
