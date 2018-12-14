import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem
import util.getFullName


class Item (
      val dataCRC: Int
    , val dataSize: DataSize
    , val modifiedDate: Date
    , val path: JointPath
    , val parentID: ItemID
    , val idInArchive: ItemIndex
    , val parentArchiveSetID: ArchiveSetID
) {
    val id: ItemID

    companion object {
        var serialCount = 1
    }

    init {
        id = serialCount
        serialCount += 1
    }

    fun getFullName() = path.last().getFullName()

    fun generateItemKey() = ItemKey(dataCRC, dataSize, 1)
    fun generateItemKey(dupCount: Int) = ItemKey(dataCRC, dataSize, dupCount)
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

typealias ItemIndex = Int
typealias ItemID = Int
typealias Date = Long
typealias Name = String
typealias DataSize = Long
