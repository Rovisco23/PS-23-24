package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.LogInputModel
import java.sql.Timestamp
import java.util.*

interface LogRepository {
    fun createLog(log: LogInputModel, createdAt: Timestamp, author: Int, images: List<FileModel>?, docs: List<FileModel>?)
    fun getById(id: Int): LogEntry?
    fun checkUserAccess(workId: UUID, userId: Int): Boolean
}