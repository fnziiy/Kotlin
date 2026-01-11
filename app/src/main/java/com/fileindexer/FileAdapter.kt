package com.fileindexer

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(private var filesList: List<FileItem>) : 
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.textFileName)
        val fileSize: TextView = view.findViewById(R.id.textFileSize)
        val fileDate: TextView = view.findViewById(R.id.textFileDate)
        val filePath: TextView = view.findViewById(R.id.textFilePath)

        fun bind(fileItem: FileItem) {
            fileName.text = fileItem.name
            fileSize.text = fileItem.getSizeFormatted()
            fileDate.text = dateFormat.format(Date(fileItem.lastModified))
            filePath.text = fileItem.path

            itemView.setOnClickListener {
                openFile(fileItem)
            }
        }

        private fun openFile(fileItem: FileItem) {
            try {
                val uri: Uri = FileProvider.getUriForFile(
                    itemView.context,
                    "${itemView.context.packageName}.provider",
                    fileItem.file
                )

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, getMimeType(fileItem.file.extension))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                itemView.context.startActivity(Intent.createChooser(intent, "فتح الملف بواسطة"))
            } catch (e: Exception) {
                Toast.makeText(
                    itemView.context,
                    "لا يمكن فتح الملف: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private fun getMimeType(extension: String): String {
            return when (extension.lowercase()) {
                "pdf" -> "application/pdf"
                "doc", "docx" -> "application/msword"
                "xls", "xlsx" -> "application/vnd.ms-excel"
                "ppt", "pptx" -> "application/vnd.ms-powerpoint"
                else -> "application/*"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(filesList[position])
    }

    override fun getItemCount() = filesList.size

    fun updateFiles(newFiles: List<FileItem>) {
        filesList = newFiles
        notifyDataSetChanged()
    }
}
