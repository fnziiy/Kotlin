package com.fileindexer

import java.io.File

data class FileItem(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val size: Long = file.length(),
    val lastModified: Long = file.lastModified()
) {
    fun getSizeFormatted(): String {
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size B"
        }
    }
}
