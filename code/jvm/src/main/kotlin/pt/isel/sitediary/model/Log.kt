package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Author
import java.util.*

data class LogInputModel(
    val workId: UUID,
    val description: String
)

data class LogCredentialsModel(
    val logId: Int,
    val workId: UUID,
    val files: List<FileOutputModel>
)

data class LogOutputModel(
    val id: Int,
    val workId: UUID,
    val author: Author,
    val content: String,
    val editable: Boolean,
    val createdAt: Date,
    val modifiedAt: Date?,
    val files: List<FileOutputModel>
)

data class FileOutputModel(
    val id: Int,
    val fileName: String,
    val contentType: String,
    val uploadDate: Date
)

data class DeleteFileModel(
    val logId: Int,
    val fileId: Int,
    val type: String
)