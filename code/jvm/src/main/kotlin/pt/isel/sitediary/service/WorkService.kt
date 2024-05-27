package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.user.checkAdmin
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkState.IN_PROGRESS
import pt.isel.sitediary.model.Invite
import pt.isel.sitediary.model.MemberInputModel
import pt.isel.sitediary.model.OpeningTermInputModel
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

    fun createWork(openingTerm: OpeningTermInputModel, user: User): CreateWorkResult = transactionManager.run {
        val workRep = it.workRepository
        val addressRep = it.addressRepository
        if (openingTerm.checkParams()) {
            failure(Errors.invalidParameter)
        } else {
            val location =
                addressRep.getLocation(openingTerm.address.location.parish, openingTerm.address.location.county)
            if (location == null) {
                failure(Errors.invalidLocation)
            } else {
                val work = Work(
                    id = UUID.randomUUID(),
                    name = openingTerm.name,
                    description = openingTerm.description ?: "",
                    state = IN_PROGRESS,
                    type = openingTerm.type,
                    address = Address(
                        Location(
                            location.district,
                            location.county,
                            location.parish
                        ),
                        openingTerm.address.street,
                        openingTerm.address.postalCode
                    ),
                    members = listOf(user.toMember()),
                    log = emptyList()
                )
                workRep.createWork(work, openingTerm, user)
                success(work)
            }
        }
    }

    fun getWork(id: UUID, userId: Int) = transactionManager.run {
        val work = it.workRepository.getById(id)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else {
            success(work)
        }
    }

    fun getWorkList(skip: Int, userId: Int) = transactionManager.run {
        val work = it.workRepository.getWorkList(skip, userId) // List with next 6 Work for pagination
        success(work)
    }

    fun inviteMembers(members: List<MemberInputModel>, workId: UUID, userId: Int) {
        transactionManager.run {
            val user = it.usersRepository.getUserById(userId)
            val work = it.workRepository.getById(workId)
            if (user == null) {
                failure(Errors.userNotFound)
            } else if (work == null) {
                failure(Errors.workNotFound)
            } else if (!work.members.checkAdmin(userId)) {
                failure(Errors.notAdmin)
            } else {
                val list = mutableListOf<Invite>()
                for (m in members) {
                    if (!it.workRepository.checkInvite(workId, m.email)) {
                        val invite = Invite(UUID.randomUUID(), m.email, m.role, workId)
                        list.add(invite)
                    }
                }
                if (list.isNotEmpty()) {
                    it.workRepository.inviteMembers(list)
                    list.forEach{i -> work.createInvites(i)}
                }
                success("Invites sent successfully")
            }
        }
    }

    fun getOpeningTerm(workId: UUID, userId: Int) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else {
            val openingTerm = workRep.getOpeningTerm(workId)
            success(openingTerm)
        }
    }

    fun finishWork(workId: UUID, userId: Int) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.checkAdmin(userId)) {
            failure(Errors.notAdmin)
        } else {
            TODO(
                "Falar com os orientadores sobre como seria a melhor abordagem para finalizar uma obra " +
                        "seguindo as informações neste site: https://www.imovel.pt/news/imobiliario/livro-de-obra"
            )
            //workRep.finishWork(workId)
        }
    }
}
