package archive

import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.IOException
import java.io.RandomAccessFile
import java.io.FileNotFoundException
import java.util.HashMap

import util.*


data class ArchiveAndStream (val inArchive: IInArchive, val inStream: IInStream, var randomAccess: RandomAccessFile?, var archiveCallback: ArchiveOpenVolumeCallback?)

fun openArchive(aFilePath: String): ArchiveAndStream {
    return if (aFilePath.isSingleVolume())
        openSingleVolumeArchive(aFilePath)
        else openMultiVolumeArchive(aFilePath)
}

fun openSingleVolumeArchive(aFilePath: String): ArchiveAndStream {
    println("Open single volume with $aFilePath")

    var randomAccessFile: RandomAccessFile? = null
    var inArchive: IInArchive? = null
    var inStream: IInStream? = null
    try {
        randomAccessFile = RandomAccessFile(aFilePath, "r")
        inStream = RandomAccessFileInStream(randomAccessFile)
    } catch (e: Exception) {
        System.err.println(String.format("[Error]<openArchive>: Fail to open RandomAccessFile with $aFilePath\n%s", e.toString()))
        throw e
    }
    try {
        inArchive = SevenZip.openInArchive(
            null,
            inStream
        )
    } catch (e: Exception) {
        randomAccessFile.close()
        System.err.println(String.format("[Error]<openArchive>: Fail to open InArchive with $aFilePath\n%s", e.toString()))
        throw e
    }
    return ArchiveAndStream(inArchive, inStream, randomAccessFile, null)
}


fun openMultiVolumeArchive(aFilePath : String): ArchiveAndStream {
    println("Open multi-volume with $aFilePath")

    var archiveOpenVolumeCallback: ArchiveOpenVolumeCallback? = null
    var inArchive: IInArchive? = null
    val inStream: IInStream?
    try {
        archiveOpenVolumeCallback = ArchiveOpenVolumeCallback()
        inStream = archiveOpenVolumeCallback.getStream(aFilePath)
    } catch (e: Exception) {
        System.err.println(String.format("[Error]<openMultiVolumeArchive>: Fail to open IInStream with $aFilePath\n%s", e.toString()))
        throw e
    }
    try {
        inArchive = SevenZip.openInArchive(
            null, inStream!!,
            archiveOpenVolumeCallback
        )
    } catch (e: Exception) {
        archiveOpenVolumeCallback.close()
        System.err.println(String.format("[Error]<openArchive>: Fail to open InArchive with $aFilePath\n%s", e.toString()))
        throw e
    }
    return ArchiveAndStream(inArchive, inStream, null, archiveOpenVolumeCallback)
}

fun closeArchiveAndStream(anANS: ArchiveAndStream) {
    if (anANS.inArchive != null) {
        try {
            anANS.inArchive.close()
        } catch (e: SevenZipException) {
            System.err.println("Error closing archive: $e")
            throw e
        }
    }
    if (anANS.randomAccess != null) {
        try {
            anANS.randomAccess?.close()
        } catch (e: IOException) {
            System.err.println("Error closing file: $e")
            throw e
        }
    }
    if (anANS.archiveCallback != null) {
        try {
            anANS.archiveCallback?.close()
        } catch (e: IOException) {
            System.err.println("Error closing file: $e")
            throw e
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
