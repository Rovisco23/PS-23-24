package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success

typealias CreateLogResult = Result<Errors, Unit>
typealias GetLogResult = Result<Errors, LogEntry>

@Component
class LogService (
    private val transactionManager: TransactionManager,
    private val clock: Clock
) {
    fun createLog(log: LogInputModel, userId: Int):CreateLogResult = transactionManager.run {
        val work = it.workRepository.getById(log.workId)
        if (work == null){
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)){
            failure(Errors.notMember)
        } else {
            val l = it.logRepository.createLog(log, clock.now(), userId)
            success(Unit)
        }
        //Verificar se é tecnico
    }

    fun getLogs() {
        TODO()
    }

    fun getLog(logId: Int, userId: Int): GetLogResult = transactionManager.run {
        val logRepository = it.logRepository
        val log = logRepository.getById(logId)
        if (log == null){
            failure(Errors.logNotFound)
        } else if (!logRepository.checkUserAccess(log.workId, userId)){
            failure(Errors.notMember)
        } else {
            success(log)
        }
    }

    fun deleteLog() {
        TODO()
    }
}