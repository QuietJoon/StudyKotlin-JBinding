package util

import java.io.File

import directoryDelimiter

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
    val archiveExts: Array<String> = arrayOf("rar", "zip", "exe")
    for ( aExt in archiveExts ) {
        if ( this.getExtension() == aExt ) {
            return true
        }
    }
    return false
}

fun String.isArchiveSensitively(): Boolean? {
    val archiveExts: Array<String> = arrayOf("rar", "zip")
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

fun String.trimming(maxLen: Int): String {
    val suffix=".."

    if (this.length > maxLen) return this.substring(0,maxLen-suffix.length) + suffix
    else return this
}

fun String.regulating(len: Int): String {
    val suffix=".."
    val prefix=" "

    return when {
        this.length < len -> " ".repeat(len-this.length)+this
        this.length > len -> this.substring(0,len-suffix.length) + suffix
        else -> this
    }
}
