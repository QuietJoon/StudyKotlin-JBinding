import net.sf.sevenzipjbinding.IInArchive

class ArchiveSet (
      val originalArchiveSetPaths: Array<RealPath>
    , val archiveSetID: ArchiveSetID
    , val superArchiveSetID: ArchiveSetID
    , val inArchive: IInArchive
    , var itemList: ItemTable
    , var subArchiveSetList: List<ArchiveSet>
) {
}

typealias ArchiveSetID = Int

val rootArchiveSetID = 0
