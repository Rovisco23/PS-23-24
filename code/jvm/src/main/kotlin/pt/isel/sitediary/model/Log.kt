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
    val contentType: String
)

data class LogOutputModel(
    val id: Int,
    val author: Author,
    val title: String,
    val content: String,
    val state: String,
    val createdAt: Date,
    val lastModifiedAt: Date?
)