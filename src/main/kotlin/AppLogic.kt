import java.io.File

import util.*


typealias RawFilePathAnalyzed = Triple<RealPath,String,Array<RealPath>>

fun rawFilePathAnalyze(files: List<File>): RawFilePathAnalyzed {
    val pathArray = files.map{it.toString()}.toTypedArray()
    val firstOrSinglePaths = getFirstOrSingleArchivePaths(pathArray)
    val paths = firstOrSinglePaths.map{it.getFullName()}.joinToString(separator = "\n")
    var colorName = if (firstOrSinglePaths.size == 1) "Red" else "Green"

    return Triple (paths, colorName, firstOrSinglePaths)
}
