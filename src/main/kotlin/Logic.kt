import util.*

fun getFirstOrSingleArchivePaths(paths: Array<String>) : Array<String> {
    var firstOrSingle: MutableList<String> = mutableListOf()
    for ( aPath in paths ) {
        if ( aPath.isArchive() ) {
            // I knew this can be replaced by single if by using `maybePartNumber`
            // But I want to leave this structure for easy reading
            if (aPath.isSingleVolume()) {
                firstOrSingle.add(aPath)
            } else if (aPath.isFirstVolume()) {
                firstOrSingle.add(aPath)
            }
        }
    }
    return firstOrSingle.toTypedArray()
}
