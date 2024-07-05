package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.User
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

typealias CreateLogResult = Result<Errors, Int>
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
            val logId = if (files != null) {
                val docs = files.filter { f -> f.fileName.endsWith(".pdf") }
                val images = files.filter { f -> f.contentType.startsWith("image") }
                it.logRepository.createLog(log, Timestamp.from(clock.now().toJavaInstant()), userId, images, docs)
            } else {
                it.logRepository.createLog(log, Timestamp.from(clock.now().toJavaInstant()), userId, null, null)
            }
            success(logId)
        }
    }

    fun getLog(logId: Int, user: User): GetLogResult = transactionManager.run {
        val logRepository = it.logRepository
        val log = logRepository.getById(logId)
        if (log == null) {
            failure(Errors.logNotFound)
        } else if (user.role != "ADMIN" && !logRepository.checkUserAccess(log.workId, user.id)) {
            failure(Errors.notMember)
        } else {
            if (log.editable && checkIfEditTimeElapsed(log.createdAt)) {
                logRepository.finish(logId)
                val aux = log.copy(editable = false)
                success(aux)
            } else success(log)
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
            if (logEntry.editable && checkIfEditTimeElapsed(logEntry.createdAt)) logRepository.finish(log.logId)
            val images = log.files.filter { f -> f.contentType == "Imagem" }.map { img -> img.id }
            val documents = log.files.filter { f -> f.contentType == "Documento" }.map { doc -> doc.id }
            val files = logRepository.getFiles(images, documents)
            success(files)
        }
    }

    fun editLog(logId: Int, logInfo: LogInputModel, listOfFiles: List<FileModel>?, userId: Int) =
        transactionManager.run {
            val logRepository = it.logRepository
            val logEntry = logRepository.getById(logId)
            if (logEntry == null) {
                failure(Errors.logNotFound)
            } else if (!logEntry.editable) {
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
        val elapsedTime = Duration.between(createdAt.toInstant(), clock.now().toJavaInstant()).toMillis()
        return elapsedTime >= Duration.ofHours(3).toMillis()
    }

    fun deleteFile(body: LogCredentialsModel, userId: Int) = transactionManager.run {
        val logRepository = it.logRepository
        val logEntry = logRepository.getById(body.logId)
        if (logEntry == null) {
            failure(Errors.logNotFound)
        } else if (!logRepository.checkUserAccess(logEntry.workId, userId)) {
            failure(Errors.notMember)
        } else if (!logEntry.editable) {
            failure(Errors.logNotEditable)
        } else if (checkIfEditTimeElapsed(logEntry.createdAt)) {
            logRepository.finish(body.logId)
            failure(Errors.logNotEditable)
        } else {
            val images = body.files.filter { f -> f.contentType == "Imagem" }.map { img -> img.id }
            val documents = body.files.filter { f -> f.contentType == "Documento" }.map { doc -> doc.id }
            logRepository.deleteFiles(images, documents)
            success(Unit)
        }
    }

    fun getMyLogs(user: User) = transactionManager.run {
        val logs = it.logRepository.getMyLogs(user.id)
        success(logs)
    }
}