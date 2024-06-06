package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Author
import java.util.*

data class LogInputModel(
    val workId: UUID,
    //image or document
    val title: String,
    val description: String
)

data class LogCredentialsModel(
    val logId: Int,
    val workId: UUID,
    val files: List<FileOutputModel>
)

data class LogOutputModel(
    val workId: UUID,
    val author: Author,
    val title: String,
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
)