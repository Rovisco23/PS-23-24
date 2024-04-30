package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.success
import pt.isel.sitediary.domainmodel.user.containsMemberById
import java.util.*

@Component
class WorkService(
    private val transactionManager: TransactionManager,
    private val clock: Clock
) {
    fun createWork(userId: Int) = transactionManager.run {
        val rep = it.workRepository

    }

    fun getWork(id: UUID, userId: Int) = transactionManager.run {
        val work = it.workRepository.getById(id)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else {
            success(work)
        }
    }

    fun getWorkList(skip: Int, userId: Int) = transactionManager.run {
        val work = it.workRepository.getWorkList(skip, userId) // List with next 6 Work for pagination
        success(work)
    }
}
