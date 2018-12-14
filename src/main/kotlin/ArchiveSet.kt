import net.sf.sevenzipjbinding.IInArchive

class ArchiveSet (
      val originalArchiveSetPaths: Array<RealPath>
    , val archiveSetID: ArchiveSetID
    , val superArchiveSetID: ArchiveSetID
    , val inArchive: IInArchive
) {
    companion object {
        lateinit var itemList: ItemTable
        lateinit var subArchiveSetList: MutableList<ArchiveSet>
    }
    init {
        itemList = mutableMapOf()
        subArchiveSetList = mutableListOf()
    }
}

typealias ArchiveSetID = Int

val rootArchiveSetID = 0
