import java.io.File

import archive.*
import archive.ArchiveAndStream
import util.*


data class RawFileAnalyzed (
    val paths : RealPath
    , val colorName: String
    , val firstOrSinglePaths: Array<RealPath>)

fun rawFileAnalyze(files: List<File>): RawFileAnalyzed {
    val paths = generateStringFromFileList(files)
    val pathArray = files.map{it.toString()}.toTypedArray()
    val firstOrSinglePaths = getFirstOrSingleArchivePaths(pathArray)
    var colorName = if (firstOrSinglePaths.size == 1) "Red" else "Green"
    var anANS: ArchiveAndStream?

    for ( aPath in firstOrSinglePaths ) {
        try {
            println("<firstPhase>: opening $aPath")
            anANS = openArchive(aPath) ?: error("[Error]<rawFileAnalyze>: Fail to open")
            //printItemList(anANS.inArchive)
            anANS.close()
        } catch (e: Exception) {
            println("[Error]<FirstPhase>: Seems to fail opening")
            colorName = "Red"
        }
    }

    return RawFileAnalyzed (paths, colorName, firstOrSinglePaths)
}
