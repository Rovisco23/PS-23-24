package pt.isel.sitediary.model

import java.util.UUID

data class LogInputModel(
    val workId: UUID,
    //image or document
    val description: String
)

data class Log (
    val id: Int,
    val workId: UUID,
    val userId: Int,
    val description: String,
    val state: String,
    val createdAt: Long,
    val lastUsedAt: Long
)