package pt.isel.sitediary.domain

import java.util.Date
import java.util.UUID

data class LogEntry(
    val id: Int,
    val workId: UUID,
    val author: Author,
    val title: String,
    val content: String,
    val editable: Boolean,
    val createdAt: Date,
    val modifiedAt: Date,
    val files: List<FileModel>
)

data class LogEntrySimplified(
    val id: Int,
    val author: Author,
    val title: String,
    val editable: Boolean,
    val createdAt: Date,
    val attachments: Boolean
)

data class Author(
    val id: Int,
    val name: String,
    val role: String
)

data class FileModel(
    val fileName: String,
    val uploadDate: Date
)

data class LogInputModel(
    val title: String,
    val description: String,
)