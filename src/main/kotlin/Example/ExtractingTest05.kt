/*
Copy from https://gist.github.com/borisbrodski/6120309
 */

import archive.*

fun main(args: Array<String>) {
    var test = false
    var filter: String? = null
    val theArchivePaths = arrayOf(
        "R:\\TestArchives\\WhereIs.rar"
        , "R:\\TestArchives\\MultiVolume.part1.rar"
        , "R:\\TestArchives\\SingleVolume.rar"
        , "R:\\TestArchives\\SingleVolume.zip"
    )
    var inputList = mutableListOf<Pair<String,String>>()
    theArchivePaths.forEachIndexed { index, aPath ->
        inputList.add(Pair(aPath,"R:\\TestArchives\\Output\\"+index.toString()))
    }

    for (aPair in inputList) {
        try {
            println(aPair.first)
            var anANS = openArchive(aPair.first)
            val ids = getNestedArchivesIDArray(anANS)
            val extract = Extract(aPair.first, aPair.second, test, filter)
            extract.prepareOutputDirectory()
            extract.extractSomething(anANS,ids)
            println("Extraction successful")

        } catch (e: ExtractionException) {
            System.err.println("[ERROR]<Main>: " + e.localizedMessage)
            e.printStackTrace()
        }
    }
}
