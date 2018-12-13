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


class ArchiveOpenVolumeCallback : IArchiveOpenVolumeCallback, IArchiveOpenCallback {

    /**
     * Cache for opened file streams
     */
    private val openedRandomAccessFileList = HashMap<String, RandomAccessFile>()

    /**
     * Name of the last volume returned by [.getStream]
     */
    private var name: String? = null

    /**
     * This method should at least provide the name of the last
     * opened volume (propID=PropID.NAME).
     *
     * @see IArchiveOpenVolumeCallback.getProperty
     */
    @Throws(SevenZipException::class)
    override fun getProperty(propID: PropID): Any? {
        when (propID) {
            PropID.NAME -> return name
        }
        return null
    }

    /**
     * The name of the required volume will be calculated out of the
     * name of the first volume and a volume index. In case of RAR file,
     * the substring ".partNN." in the name of the volume file will
     * indicate a volume with id NN. For example:
     *
     *  * test.rar - single part archive or multi-part archive with
     * a single volume
     *  * test.part23.rar - 23-th part of a multi-part archive
     *  * test.part001.rar - first part of a multi-part archive.
     * "00" indicates, that at least 100 volumes must exist.
     *
     */
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
            // Required volume doesn't exist. This happens if the volume:
            // 1. never exists. 7-Zip doesn't know how many volumes should
            //    exist, so it have to try each volume.
            // 2. should be there, but doesn't. This is an error case.

            // Since normal and error cases are possible,
            // we can't throw an error message
            return null // We return always null in this case
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    /**
     * Close all opened streams
     */
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
