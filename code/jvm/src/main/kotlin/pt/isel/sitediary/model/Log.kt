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
    val workId: UUID
)

data class LogOutputModel(
    val workId: UUID,
    val author: Author,
    val title: String,
    val content: String,
    val state: String,
    val createdAt: Date,
    val modifiedAt: Date?
)