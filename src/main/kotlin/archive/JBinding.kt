package archive

import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.IOException
import java.io.RandomAccessFile


fun jBindingChecker(): Boolean{
    try {
        SevenZip.initSevenZipFromPlatformJAR()
        return true
    } catch (e: SevenZipNativeInitializationException) {
        println("Fail to initialize 7-Zip-JBinding library")
        e.printStackTrace()
        return false
    }
}

fun archiveOpener(aFile: String) {
    println("archiveOpener with $aFile")
    
    var randomAccessFile: RandomAccessFile? = null
    var inArchive: IInArchive? = null
    try {
        randomAccessFile = RandomAccessFile(aFile, "r")
        inArchive = SevenZip.openInArchive(null, // autodetect archive type
                RandomAccessFileInStream(randomAccessFile))

        // Getting simple interface of the archive inArchive
        val simpleInArchive = inArchive!!.getSimpleInterface()
        val theSize = simpleInArchive.archiveItems.size

        println("Archive item size: $theSize")

        println(String.format("Archive Type: %s", inArchive.archiveFormat.toString()))
        // TODO: How to get CRC which is not of item but of archive itself
        /*
        val archiveCRC = inArchive.getArchiveProperty(PropID.CRC)
        println(String.format("Archive CRC: %08X", archiveCRC))
        val archiveCHECKSUM = inArchive.getArchiveProperty(PropID.CHECKSUM)
        println(String.format("Archive CHECKSUM: %08X", archiveCHECKSUM))
        */

        println("   CRC    |   Size    | Compr.Sz. | Filename")
        println("----------+-----------+-----------+---------")

        for (item in simpleInArchive.archiveItems) {
            println(String.format(" %08X | %9s | %9s | %s", //
                    item.crc,
                    item.size,
                    item.packedSize,
                    item.path))
        }
    } catch (e: Exception) {
        System.err.println(String.format("[Error]<testOpener>: %s", e.toString()))
        throw e
    } finally {
        if (inArchive != null) {
            try {
                inArchive.close()
            } catch (e: SevenZipException) {
                System.err.println("Error closing archive: $e")
            }

        }
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close()
            } catch (e: IOException) {
                System.err.println("Error closing file: $e")
            }

        }
    }
}
