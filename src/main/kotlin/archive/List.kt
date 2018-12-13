package archive

import net.sf.sevenzipjbinding.PropID
import util.isArchive


fun listItems(ans: ArchiveAndStream) {
    // Getting simple interface of the archive inArchive
    val simpleInArchive = ans.inArchive!!.getSimpleInterface()
    val theSize = simpleInArchive.archiveItems.size

    println("Archive item size: $theSize")

    println(String.format("Archive Format: %s", ans.inArchive.archiveFormat.toString()))

    println("  ID  |   CRC    |   Size    | Compr.Sz. | Filename")
    println("-----------------+-----------+-----------+---------")

    for (item in simpleInArchive.archiveItems) {
        println(
            String.format(
                " %4d | %08X | %9s | %9s | %s", //
                item.itemIndex,
                item.crc,
                item.size,
                item.packedSize,
                item.path
            )
        )
    }
}

fun getNestedArchivesIDArray(ans: ArchiveAndStream): IntArray {
    val simpleInArchive = ans.inArchive!!.getSimpleInterface()
    var idList = mutableListOf<Int>()

    for (item in simpleInArchive.archiveItems) {
        if (item.path.toString().isArchive()) {
            idList.add(item.itemIndex)
        }
    }

    return idList.toIntArray()
}
