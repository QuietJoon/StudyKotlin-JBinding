package archive


fun listItems(ans: ArchiveAndStream) {
    // Getting simple interface of the archive inArchive
    val simpleInArchive = ans.inArchive!!.getSimpleInterface()
    val theSize = simpleInArchive.archiveItems.size

    println("Archive item size: $theSize")

    println(String.format("Archive Format: %s", ans.inArchive.archiveFormat.toString()))

    println("   CRC    |   Size    | Compr.Sz. | Filename")
    println("----------+-----------+-----------+---------")

    for (item in simpleInArchive.archiveItems) {
        println(
            String.format(
                " %08X | %9s | %9s | %s", //
                item.crc,
                item.size,
                item.packedSize,
                item.path
            )
        )
    }
}