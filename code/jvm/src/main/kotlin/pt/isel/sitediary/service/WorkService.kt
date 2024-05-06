package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkState.IN_PROGRESS
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success
import java.util.*

typealias CreateWorkResult = Result<Errors, Work>

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
