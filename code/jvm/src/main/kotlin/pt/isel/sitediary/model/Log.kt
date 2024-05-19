package pt.isel.sitediary.model

import java.util.UUID

data class LogInputModel(
    val workId: UUID,
    //image or document
    val description: String
)