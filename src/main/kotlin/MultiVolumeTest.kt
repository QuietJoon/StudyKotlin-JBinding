import net.sf.sevenzipjbinding.*
import java.io.IOException

import archive.*


fun main(args : Array<String>) {
    println("Multi-volume RAR Test")

    val theFilePath = "R:\\TestArchives\\MultiVolume.part1.rar"

    var archiveOpenVolumeCallback: ArchiveOpenVolumeCallback? = null
    var inArchive: IInArchive? = null
    try {

        archiveOpenVolumeCallback = ArchiveOpenVolumeCallback()
        val inStream = archiveOpenVolumeCallback.getStream(theFilePath)
        inArchive = SevenZip.openInArchive(
            null , inStream!!,
            archiveOpenVolumeCallback
        )

        println(String.format("Archive Format: %s", inArchive.archiveFormat.toString()))

        println("   Size   | Compr.Sz. | Filename")
        println("----------+-----------+---------")
        val itemCount = inArchive!!.numberOfItems
        for (i in 0 until itemCount) {
            println(
                String.format(
                    "%9s | %9s | %s",
                    inArchive.getProperty(i, PropID.SIZE),
                    inArchive.getProperty(i, PropID.PACKED_SIZE),
                    inArchive.getProperty(i, PropID.PATH)
                )
            )
        }
    } catch (e: Exception) {
        System.err.println("Error occurs: $e")
    } finally {
        if (inArchive != null) {
            try {
                inArchive.close()
            } catch (e: SevenZipException) {
                System.err.println("Error closing archive: $e")
            }

        }
        if (archiveOpenVolumeCallback != null) {
            try {
                archiveOpenVolumeCallback.close()
            } catch (e: IOException) {
                System.err.println("Error closing file: $e")
            }

        }
    }
    println("End")
}
