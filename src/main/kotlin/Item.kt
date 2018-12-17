import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem

import util.*


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

    fun generateItemKey() = ItemKey(path.last().isArchiveSensitively(),dataCRC, dataSize, 1)
    fun generateItemKey(dupCount: Int) = ItemKey(path.last().isArchiveSensitively(),dataCRC, dataSize, dupCount)


    fun makeItemRecordFromItem(archiveSetNum: Int, rootArchiveSetID: ArchiveSetID,theArchiveSetID: ArchiveSetID): ItemRecord {
        val existance = arrayOfNulls<ExistanceMark>(archiveSetNum)
        existance[rootArchiveSetID]=Pair(theArchiveSetID,id)
        return ItemRecord(
            dataCRC = this.dataCRC
            , dataSize = this.dataSize
            , modifiedDate = this.modifiedDate
            , path = this.path.last()
            , existance = existance
            , isFilled = false
            , isArchive = this.path.last().isArchiveSensitively()
        )
    }

    private fun checkArchiveName(fullName: String): Boolean? =
        if ( fullName.getExtension() == "exe" ) null // Make more logic
        else if ( fullName.isArchive() ) true
        else false

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Item
        return dataCRC != that.dataCRC &&
                dataSize != that.dataSize &&
                modifiedDate != that.modifiedDate &&
                path != that.path
    }

    override fun hashCode(): Int {
        var hash = 1
        hash = hash * hashPrime + dataCRC.hashCode()
        hash = hash * hashPrime + dataSize.hashCode()
        hash = hash * hashPrime + path.hashCode()
        hash = hash * hashPrime + parentID.hashCode()
        hash = hash * hashPrime + idInArchive.hashCode()
        hash = hash * hashPrime + parentArchiveSetID.hashCode()
        hash = hash * hashPrime + id.hashCode()
        return hash
    }
}

fun ISimpleInArchiveItem.makeItemFromArchiveItem(parentPath: JointPath, parentID: ItemID, parentArchiveSetID: ArchiveSetID): Item {

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
