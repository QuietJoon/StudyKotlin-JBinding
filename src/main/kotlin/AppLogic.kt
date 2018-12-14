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
    var colorName = if (files.size == 1) "Yellow" else "Green"
    val pathArray = files.map{it.toString()}.toTypedArray()
    val firstOrSinglePaths = getFirstOrSingleArchivePaths(pathArray)
    var anANS: ArchiveAndStream

    for ( aPath in firstOrSinglePaths ) {
        try {
            println("<firstPhase>: opening $aPath")
            anANS = openArchive(aPath)
            printItemList(anANS.inArchive)
            anANS.close()
        } catch (e: Exception) {
            println("[Error]<FirstPhase>: Seems to fail opening")
            colorName = "Red"
        }
    }

    return RawFileAnalyzed (paths, colorName, firstOrSinglePaths)
}
