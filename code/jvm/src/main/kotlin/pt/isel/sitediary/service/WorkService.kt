package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.user.checkOwner
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.WorkInput
import pt.isel.sitediary.domainmodel.work.WorkState.IN_PROGRESS
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.InviteInputModel
import pt.isel.sitediary.model.MemberInputModel
import pt.isel.sitediary.model.OpeningTermInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success
import java.util.*

typealias CreateWorkResult = Result<Errors, Unit>

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
                val work = WorkInput(
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
                success(Unit)
            }
        }
    }

    fun getWork(id: UUID, user: User) = transactionManager.run {
        val work = it.workRepository.getById(id)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (user.role != "ADMIN" && !work.members.containsMemberById(user.id)) {
            failure(Errors.notMember)
        } else {
            success(work)
        }
    }

    fun getWorkList(skip: Int, user: User) = transactionManager.run {
        val work = when (user.role) {
            "ADMIN" -> it.workRepository.getWorkListAdmin(skip)
            "CÂMARA" -> it.workRepository.getWorkListCouncil(skip, user.location)
            else -> it.workRepository.getWorkList(skip, user.id)
        }
        success(work)
    }

    fun inviteMembers(members: List<MemberInputModel>, workId: UUID, userId: Int) = transactionManager.run {
        val userRep = it.usersRepository
        val workRep = it.workRepository
        val user = userRep.getUserById(userId)
        val work = workRep.getById(workId)
        if (user == null) {
            failure(Errors.userNotFound)
        } else if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.checkOwner(userId)) {
            failure(Errors.notAdmin)
        } else {
            members.forEach { m ->
                val id = userRep.getUserByEmail(m.email)?.id
                if (id != null) {
                    workRep.inviteMember(id, m.role, workId)
                } else {
                    val dummyId = userRep.createDummyUser(m.email)
                    workRep.inviteMember(dummyId, m.role, workId)
                }
            }
            success(Unit)
        }
    }


    fun getInviteList(userId: Int) = transactionManager.run {
        val user = it.usersRepository.getUserById(userId)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            val invites = it.workRepository.getInviteList(userId)
            success(invites)
        }
    }

    fun getInvite(workId: UUID, userId: Int) = transactionManager.run {
        val user = it.usersRepository.getUserById(userId)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            val invite = it.workRepository.getInvite(workId, userId)
            if (invite == null) {
                failure(Errors.inviteNotFound)
            } else {
                success(invite)
            }
        }
    }

    fun answerInvite(inviteInput: InviteInputModel, user: User) = transactionManager.run {
        val workRep = it.workRepository
        val invite = workRep.getInvite(inviteInput.workId, user.id)
        if (invite == null) {
            failure(Errors.inviteNotFound)
        } else {
            if (inviteInput.accepted) {
                workRep.acceptInvite(invite, user)
                success(Unit)
            } else {
                workRep.declineInvite(inviteInput.workId, user.id)
                success(Unit)
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
        } else if (!work.members.checkOwner(userId)) {
            failure(Errors.notAdmin)
        } else if (work.state != IN_PROGRESS) {
            failure(Errors.workAlreadyFinished)
        } else {
            val fiscalId = work.members.firstOrNull { m -> m.role == "FISCALIZAÇÃO" }?.id
            val directorId = work.members.firstOrNull { m -> m.role == "DIRETOR" }?.id
            if (fiscalId == null || directorId == null) {
                failure(Errors.membersMissing)
            } else {
                workRep.finishWork(workId, fiscalId, directorId)
                success(Unit)
            }
        }
    }

    fun getWorkImage(workId: UUID, userId: Int) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else {
            val file = workRep.getWorkImage(workId)
            success(file)
        }
    }

    fun changeWorkImage(workId: UUID, featuredImage: FileModel?, userId: Int) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (!work.members.containsMemberById(userId)) {
            failure(Errors.notMember)
        } else {
            if (workRep.checkWorkImageExists(workId) == null && featuredImage != null) {
                workRep.insertWorkImage(workId, featuredImage)
            } else {
                if (featuredImage != null) workRep.changeWorkImage(workId, featuredImage) else workRep.removeWorkImage(
                    workId
                )
            }
            success(Unit)
        }
    }
}
