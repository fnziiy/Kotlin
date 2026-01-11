package com.fileindexer

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileScanner(private val context: Context) {

    suspend fun scanFiles(extensions: List<String>): List<FileItem> = withContext(Dispatchers.IO) {
        val filesList = mutableListOf<FileItem>()
        val storageDirectories = getStorageDirectories()
        
        storageDirectories.forEach { directory ->
            scanDirectory(directory, extensions, filesList)
        }
        
        filesList.sortedByDescending { it.lastModified }
    }

    private fun getStorageDirectories(): List<File> {
        val directories = mutableListOf<File>()
        
        // Internal Storage
        val internalStorage = Environment.getExternalStorageDirectory()
        if (internalStorage.exists()) {
            directories.add(internalStorage)
        }
        
        // External SD Card
        val externalStorages = context.getExternalFilesDirs(null)
        externalStorages.forEach { file ->
            file?.let {
                val path = it.absolutePath.split("/Android").firstOrNull()
                path?.let { p -> 
                    val extStorage = File(p)
                    if (extStorage.exists() && !directories.contains(extStorage)) {
                        directories.add(extStorage)
                    }
                }
            }
        }
        
        return directories
    }

    private fun scanDirectory(
        directory: File,
        extensions: List<String>,
        filesList: MutableList<FileItem>
    ) {
        try {
            if (!directory.canRead()) return
            
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    // تجنب المجلدات المخفية ومجلدات النظام
                    if (!file.name.startsWith(".") && 
                        !file.absolutePath.contains("/Android/data") &&
                        !file.absolutePath.contains("/Android/obb")) {
                        scanDirectory(file, extensions, filesList)
                    }
                } else {
                    val fileExtension = file.extension.lowercase()
                    if (extensions.contains(fileExtension)) {
                        filesList.add(FileItem(file))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val PDF_EXTENSIONS = listOf("pdf")
        val EXCEL_EXTENSIONS = listOf("xls", "xlsx", "xlsm", "xlsb", "csv")
        val WORD_EXTENSIONS = listOf("doc", "docx", "docm", "dot", "dotx")
        val POWERPOINT_EXTENSIONS = listOf("ppt", "pptx", "pptm", "ppsx", "pps")
    }
}
