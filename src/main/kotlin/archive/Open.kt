package archive

import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.IOException
import java.io.RandomAccessFile
import java.io.FileNotFoundException
import java.util.HashMap


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

fun archiveOpener(aFilePath : String) {
    println("archiveOpener with $aFilePath")
    
    var randomAccessFile: RandomAccessFile? = null
    var inArchive: IInArchive? = null
    try {
        randomAccessFile = RandomAccessFile(aFilePath, "r")
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

fun multiVolumeArchiveOpener(aFilePath : String) {
    println("Multi-volume opener with $aFilePath")

    var archiveOpenVolumeCallback: ArchiveOpenVolumeCallback? = null
    var inArchive: IInArchive? = null
    try {
        archiveOpenVolumeCallback = ArchiveOpenVolumeCallback()
        val inStream = archiveOpenVolumeCallback.getStream(aFilePath)
        inArchive = SevenZip.openInArchive(
            null , inStream!!,
            archiveOpenVolumeCallback)

        // Getting simple interface of the archive inArchive
        val simpleInArchive = inArchive!!.getSimpleInterface()
        val theSize = simpleInArchive.archiveItems.size

        println("Archive item size: $theSize")

        println(String.format("Archive Format: %s", inArchive.archiveFormat.toString()))

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
}

class ArchiveOpenVolumeCallback : IArchiveOpenVolumeCallback, IArchiveOpenCallback {

    private val openedRandomAccessFileList = HashMap<String, RandomAccessFile>()
    private var name: String? = null

    @Throws(SevenZipException::class)
    override fun getProperty(propID: PropID): Any? {
        when (propID) {
            PropID.NAME -> return name
        }
        return null
    }

    @Throws(SevenZipException::class)
    override fun getStream(filename: String): IInStream? {
        try {
            // We use caching of opened streams, so check cache first
            var randomAccessFile: RandomAccessFile? = openedRandomAccessFileList[filename]
            if (randomAccessFile != null) { // Cache hit.
                // Move the file pointer back to the beginning
                // in order to emulating new stream
                randomAccessFile.seek(0)

                // Save current volume name in case getProperty() will be called
                name = filename

                return RandomAccessFileInStream(randomAccessFile)
            }

            // Nothing useful in cache. Open required volume.
            randomAccessFile = RandomAccessFile(filename, "r")
            // Put new stream in the cache
            openedRandomAccessFileList[filename] = randomAccessFile
            // Save current volume name in case getProperty() will be called
            name = filename

            return RandomAccessFileInStream(randomAccessFile)
        } catch (fileNotFoundException: FileNotFoundException) {
            return null
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    @Throws(IOException::class)
    internal fun close() {
        for (file in openedRandomAccessFileList.values) {
            file.close()
        }
    }

    @Throws(SevenZipException::class)
    override fun setCompleted(files: Long?, bytes: Long?) {
    }

    @Throws(SevenZipException::class)
    override fun setTotal(files: Long?, bytes: Long?) {
    }
}
