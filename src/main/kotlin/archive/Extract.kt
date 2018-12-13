package archive

import java.io.IOException
import java.io.RandomAccessFile
import java.util.Arrays

import net.sf.sevenzipjbinding.ExtractOperationResult
import net.sf.sevenzipjbinding.IInArchive
import net.sf.sevenzipjbinding.SevenZip
import net.sf.sevenzipjbinding.SevenZipException
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream

object ExtractItemsSimple {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size == 0) {
            println("Usage: java ExtractItemsSimple <archive-name>")
            return
        }
        var randomAccessFile: RandomAccessFile? = null
        var inArchive: IInArchive? = null
        try {
            randomAccessFile = RandomAccessFile(args[0], "r")
            inArchive = SevenZip.openInArchive(
                null, // autodetect archive type
                RandomAccessFileInStream(randomAccessFile)
            )

            // Getting simple interface of the archive inArchive
            val simpleInArchive = inArchive!!.simpleInterface

            println("   Hash   |    Size    | Filename")
            println("----------+------------+---------")

            for (item in simpleInArchive.archiveItems) {
                val hash = intArrayOf(0)
                if (!item.isFolder) {
                    val result: ExtractOperationResult

                    val sizeArray = LongArray(1)
                    result = item.extractSlow { data ->
                        hash[0] = hash[0] xor Arrays.hashCode(data) // Consume data
                        sizeArray[0] += data.size.toLong()
                        data.size // Return amount of consumed data
                    }

                    if (result == ExtractOperationResult.OK) {
                        println(
                            String.format(
                                "%9X | %10s | %s",
                                hash[0], sizeArray[0], item.path
                            )
                        )
                    } else {
                        System.err.println("Error extracting item: $result")
                    }
                }
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
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close()
                } catch (e: IOException) {
                    System.err.println("Error closing file: $e")
                }

            }
        }
    }
}