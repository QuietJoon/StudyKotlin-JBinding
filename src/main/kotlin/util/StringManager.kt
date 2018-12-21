package util

import java.io.File

import directoryDelimiter
import com.ibm.icu.lang.*
import dateFormat

fun generateStringFromFileList (strings : List<File>): String {
    val internalString = strings.map{it.toString().getFullName()}.joinToString(separator = "\n")
    return arrayOf("<\n", internalString, "\n>").joinToString(separator = "")
}

fun String.getFullName(): String =
    this.substringAfterLast(directoryDelimiter)

fun String.getFileName(): String =
    this.substringAfterLast(directoryDelimiter).substringBeforeLast(".")

fun String.getExtension(): String =
    this.substringAfterLast(directoryDelimiter).substringAfterLast(".","")

fun String.getDirectory(): String =
    this.substringBeforeLast(directoryDelimiter)

fun String.isArchive(): Boolean {
    val archiveExts: Array<String> = arrayOf("rar", "zip", "7z", "exe")
    for ( aExt in archiveExts ) {
        if ( this.getExtension() == aExt ) {
            return true
        }
    }
    return false
}

fun String.isArchiveSensitively(): Boolean? {
    val archiveExts: Array<String> = arrayOf("rar", "zip", "7z")
    for ( aExt in archiveExts ) {
        if ( this.getExtension() == aExt ) {
            return true
        }
    }
    if (this.getExtension() == "exe") return null
    return false
}

fun String.isEXE() = this.getExtension() == "exe"

/*
  * null -> SingleVolume
  * 1 -> First Volume
  * otherwise -> Not single nor first volume
 */
fun String.maybePartNumber(): Int? {
    val maybeNumberString = this.substringAfterLast(".part","")
    //println(String.format("<maybePartNumber>: %s",maybeNumberString))
    return maybeNumberString.toIntOrNull()
}

fun String.isSingleVolume(): Boolean = getFileName().maybePartNumber() == null

fun String.isFirstVolume(): Boolean = getFileName().maybePartNumber() == 1

fun String.getCommonNameOfMultiVolume(): String = getFileName().substringBeforeLast(".part")

fun String.trimming(width: Int, suffix: String, suffixLength: Int): String {
    var result = ""
    var currWidth= 0
    for ( chr in this){
        val chrWidth = chr.getCharWidth()
        when {
            currWidth+chrWidth == width-suffixLength -> return result + chr + suffix.repeat(suffixLength)
            currWidth+chrWidth >  width-suffixLength -> return result + suffix.repeat(suffixLength+1)
        }
        result += chr
        currWidth += chrWidth
    }
    error("[ERROR]<trimming>: Can't be reached")
}

fun String.regulating(width: Int): String {
    val suffix="."
    val prefix=" "
    val thisWidth = this.getWidth()
    return when {
        thisWidth < width -> prefix.repeat(width-thisWidth)+this
        thisWidth > width -> this.trimming(width,suffix,2)
        else -> this
    }
}

fun String.getWidth(): Int {
    var width = 0
    this.forEach {
        width += it.getCharWidth()
    }
    return width
}

fun Char.getCharWidth(): Int {
    val width = UCharacter.getIntPropertyValue(this.toInt(), UProperty.EAST_ASIAN_WIDTH)
    return when (width) {
        UCharacter.EastAsianWidth.NARROW -> 1
        UCharacter.EastAsianWidth.NEUTRAL -> 1
        UCharacter.EastAsianWidth.HALFWIDTH -> 1
        UCharacter.EastAsianWidth.FULLWIDTH -> 2
        UCharacter.EastAsianWidth.WIDE -> 2
        else -> 1
    }
}

fun Long.dateFormatter(): String {
    return dateFormat.format(java.util.Date(this))
}
