package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.domainmodel.work.WorkState
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.LogCredentialsModel
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success
import java.sql.Timestamp
import java.time.Duration
import java.util.*

typealias CreateLogResult = Result<Errors, Unit>
typealias GetLogResult = Result<Errors, LogEntry>
typealias GetLogFilesResult = Result<Errors, List<FileModel>?>

@Component
class LogService(
    private val transactionManager: TransactionManager,
    private val clock: Clock
) {
    fun createLog(log: LogInputModel, files: List<FileModel>?, userId: Int): CreateLogResult = transactionManager.run {
        val work = it.workRepository.getById(log.workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else if (!work.members.first { m -> m.id == userId }.checkTechnician()) {
            failure(Errors.notTechnician)
        } else if (work.state == WorkState.FINISHED) {
            failure(Errors.workFinished)
        } else {
            if (files != null) {
                val docs = files.filter { f -> f.fileName.endsWith(".pdf") }
                val images = files.filter { f -> f.contentType.startsWith("image") }
                it.logRepository.createLog(log, Timestamp.from(clock.now().toJavaInstant()), userId, images, docs)
            } else {
                it.logRepository.createLog(log, Timestamp.from(clock.now().toJavaInstant()), userId, null, null)
            }
            success(Unit)
        }
    }

    fun getLog(logId: Int, userId: Int): GetLogResult = transactionManager.run {
        val logRepository = it.logRepository
        val log = logRepository.getById(logId)
        if (log == null) {
            failure(Errors.logNotFound)
        } else if (!logRepository.checkUserAccess(log.workId, userId)) {
            failure(Errors.notMember)
        } else {
            success(log)
        }
    }

    fun getLogFiles(log: LogCredentialsModel, userId: Int): GetLogFilesResult = transactionManager.run {
        val logRepository = it.logRepository
        val logEntry = logRepository.getById(log.logId)
        if (logEntry == null) {
            failure(Errors.logNotFound)
        } else if (!logRepository.checkUserAccess(logEntry.workId, userId)) {
            failure(Errors.notMember)
        } else {
            if (log.contentType != "images" && log.contentType != "docs") {
                failure(Errors.internalError)
            }
            val files = if (log.contentType == "images") logRepository.getImages(log.logId)
            else logRepository.getDocs(log.logId)
            success(files)
        }
    }

    fun editLog(logId: Int, logInfo: LogInputModel, listOfFiles: List<FileModel>?, userId: Int) =
        transactionManager.run {
            val logRepository = it.logRepository
            val logEntry = logRepository.getById(logId)
            if (logEntry == null) {
                failure(Errors.logNotFound)
            } else if (logEntry.state != "EDITÁVEL") {
                failure(Errors.logNotEditable)
            } else if (checkIfEditTimeElapsed(logEntry.createdAt)) {
                logRepository.finish(logId)
                failure(Errors.logNotEditable)
            } else if (!logRepository.checkUserAccess(logEntry.workId, userId)) {
                failure(Errors.notMember)
            } else {
                if (listOfFiles != null) {
                    val docs = listOfFiles.filter { f -> f.fileName.endsWith(".pdf") }
                    val images = listOfFiles.filter { f -> f.contentType.startsWith("image") }
                    logRepository.editLog(logId, logInfo, Timestamp.from(clock.now().toJavaInstant()), images, docs)
                } else {
                    logRepository.editLog(logId, logInfo, Timestamp.from(clock.now().toJavaInstant()), null, null)
                }
            }
            success(Unit)
        }

    private fun checkIfEditTimeElapsed(createdAt: Date): Boolean {
        val elapsedTime = Duration.between(createdAt.toInstant(), clock.now().toJavaInstant())
        return elapsedTime.toMillis() >= Duration.ofHours(1).toMillis()
    }
}