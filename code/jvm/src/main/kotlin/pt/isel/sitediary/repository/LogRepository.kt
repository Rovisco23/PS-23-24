package pt.isel.sitediary.repository

import kotlinx.datetime.Instant
import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.model.LogInputModel
import java.util.*

interface LogRepository {
    fun createLog(log: LogInputModel, createdAt: Instant, author: Int): Int
    fun getById(id: Int): LogEntry?
    fun checkUserAccess(workId: UUID, userId: Int): Boolean
}