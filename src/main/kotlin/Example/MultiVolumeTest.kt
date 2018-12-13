import archive.*


fun main(args : Array<String>) {
    println("Multi-volume RAR Test")
    if (jBindingChecker()) openMultiVolumeArchive("R:\\TestArchives\\MultiVolume.part1.rar")
    if (jBindingChecker()) openMultiVolumeArchive("R:\\TestArchives\\MissingMultiVolume.part1.rar")
    println("End")
}
