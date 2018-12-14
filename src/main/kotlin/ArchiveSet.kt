import net.sf.sevenzipjbinding.IInArchive

class ArchiveSet (
    val originalArchiveSetPaths: Array<RealPath>,
    val superArchiveSetID: ArchiveSetID,
    val inArchive: IInArchive
) {
}

typealias ArchiveSetID = Int

val rootArchiveSetID = 0
