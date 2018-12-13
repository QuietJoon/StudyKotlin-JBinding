import archive.*


fun main(args : Array<String>) {
    println("Multi-volume RAR Test")
    if (jBindingChecker()) multiVolumeArchiveOpener("R:\\TestArchives\\MultiVolume.part1.rar")
    println("End")
}
