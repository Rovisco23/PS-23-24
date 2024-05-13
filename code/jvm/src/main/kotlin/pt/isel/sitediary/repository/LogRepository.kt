package pt.isel.sitediary.repository

import kotlinx.datetime.Instant
import pt.isel.sitediary.model.LogInputModel

interface LogRepository {
    fun createLog(log: LogInputModel, createdAt: Instant, lastUsedAt: Instant, user: Int): Int
}