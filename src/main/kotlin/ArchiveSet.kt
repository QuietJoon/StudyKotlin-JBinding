import net.sf.sevenzipjbinding.IInArchive

class ArchiveSet (
    val originalArchiveSetPaths: Array<RealPath>,
    val superArchiveSetID: Int,
    val inArchive: IInArchive
) {
}
