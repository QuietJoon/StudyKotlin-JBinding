fun printIgnoringList(ignoringList: IgnoringList) {

    println("   CRC    |   Size    |         Modified Date        | Filename")
    println("----------+-----------+------------------------------+---------")

    for (item in ignoringList.ignoringList) {
        println(
            String.format(
                " %08X | %9s | %28s | %s",
                item.itemCRC.datum,
                item.itemSize.datum,
                java.util.Date(item.itemModifiedDate.datum).toString(),
                item.itemName.datum
            )
        )
    }
}

fun printIgnoringListWithLevel(ignoringList: IgnoringList) {

    println("    CRC     |    Size     |          Modified Date         | Filename")
    println("------------+-------------+--------------------------------+---------")

    for (item in ignoringList.ignoringList) {
        println(
            String.format(
                " %s%08X | %s%9s | %s%28s | %s%s",
                item.itemCRC.level.toShortString(),
                item.itemCRC.datum,
                item.itemSize.level.toShortString(),
                item.itemSize.datum,
                item.itemModifiedDate.level.toShortString(),
                java.util.Date(item.itemModifiedDate.datum).toString(),
                item.itemName.level.toShortString(),
                item.itemName.datum
            )
        )
    }
}
