package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success

typealias CreateLogResult = Result<Errors, Int>

@Component
class LogService (
    private val transactionManager: TransactionManager,
    private val clock: Clock
) {
    fun createLog(log: LogInputModel, user: Int):CreateLogResult = transactionManager.run {
        val work = it.workRepository.getById(log.workId)
        if (work == null){
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(user)){
            failure(Errors.notMember)
        } else {
            val t = TODO("Verificar se é tecnico")
            val l = it.logRepository.createLog(log, clock.now(), clock.now(), user)
            success(l)
        }
    }

    fun getLogs() {
        TODO()
    }

    fun getLog() {
        TODO()
    }

    fun deleteLog() {
        TODO()
    }
}