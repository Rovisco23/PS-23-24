package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.user.checkOwner
import pt.isel.sitediary.domainmodel.user.containsMemberById
import pt.isel.sitediary.domainmodel.work.*
import pt.isel.sitediary.domainmodel.work.WorkState.*
import pt.isel.sitediary.model.*
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success
import java.sql.Timestamp
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
        } else if (openingTerm.checkTechnicians()) {
            failure(Errors.invalidTechnicians)
        } else {
            val location =
                addressRep.getLocation(
                    openingTerm.address.location.parish,
                    openingTerm.address.location.county,
                    openingTerm.address.location.district
                )
            if (location == null) {
                failure(Errors.invalidLocation)
            } else {
                val work = WorkInput(
                    id = UUID.randomUUID(),
                    name = openingTerm.name,
                    description = openingTerm.description ?: "",
                    state = if (!openingTerm.verification.isNullOrBlank() && !openingTerm.checkCouncilWork(user)) VERIFYING else IN_PROGRESS,
                    type = WorkType.fromString(openingTerm.type) ?: WorkType.RESIDENCIAL,
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
                workRep.createWork(work, Timestamp.from(clock.now().toJavaInstant()), openingTerm, user)
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
            val workResult = work.copy(log = work.log.map { log ->
                if (log.editable && checkIfEditTimeElapsed(log.createdAt, clock)) {
                    it.logRepository.finish(log.id)
                    log.copy(editable = false)
                } else {
                    if (log.author.id != user.id) log.copy(editable = false)
                    else log
                }
            })
            success(workResult)
        }
    }

    fun getWorkList(user: User) = transactionManager.run {
        val work = when (user.role) {
            "ADMIN" -> it.workRepository.getWorkListAdmin()
            "CÂMARA" -> it.workRepository.getWorkListCouncil(user.location, user)
            else -> it.workRepository.getWorkList(user.id)
        }
        success(work)
    }

    fun getWorksPending(user: User) =
        transactionManager.run {
            val rep = it.workRepository
            if (user.role == "ADMIN") {
                val works = rep.getAllWorksPending()
                success(works)
            } else if (user.role == "CÂMARA") {
                val works = rep.getWorksPending(user.location)
                success(works)
            } else {
                failure(Errors.forbidden)
            }
        }

    fun answerPendingWork(workId: UUID, user: User, accepted: Boolean) = transactionManager.run {
        val rep = it.workRepository
        if (user.role != "ADMIN" && user.role != "CÂMARA") {
            failure(Errors.forbidden)
        } else if (accepted) {
            rep.acceptPending(workId, user.name, Timestamp.from(clock.now().toJavaInstant()))
            success(Unit)
        } else {
            rep.declinePending(workId, user.name, Timestamp.from(clock.now().toJavaInstant()))
            success(Unit)
        }
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
                    if (!work.members.containsMemberById(id)) {
                        workRep.inviteMember(id, m.role, workId)
                    }
                } else {
                    val dummyId = userRep.createDummyUser(m.email)
                    workRep.inviteMember(dummyId, m.role, workId)
                }
                work.sendEmailInvitation(Invite(UUID.randomUUID(), m.email, m.role, workId))
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


    /*fun getOpeningTerm(workId: UUID, user: User) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (user.role != "ADMIN" && !work.members.containsMemberById(user.id)) {
            failure(Errors.notMember)
        } else {
            val openingTerm = workRep.getOpeningTerm(workId)
            success(openingTerm)
        }
    }*/

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
            val requiredTechnicians = workRep.checkRequiredTechnicians(workId)
            if (!requiredTechnicians) {
                failure(Errors.membersMissing)
            } else {
                workRep.finishWork(workId)
                success(Unit)
            }
        }
    }

    fun getWorkImage(workId: UUID, user: User) = transactionManager.run {
        val workRep = it.workRepository
        val work = workRep.getById(workId)
        if (work == null) {
            failure(Errors.workNotFound)
        } else if (user.role != "ADMIN" && !work.members.containsMemberById(user.id)) {
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
        } else if (!work.members.checkOwner(userId)) {
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

    fun getNumberOfInvites(id: Int) = transactionManager.run {
        success(it.workRepository.getNumberOfInvites(id))
    }

    fun getMemberProfile(workId: String, member: String, user: User) =
        transactionManager.run {
            val rep = it.workRepository
            val work = rep.getById(UUID.fromString(workId))
            if (work == null) {
                failure(Errors.workNotFound)
            } else {
                val profile = rep.getMemberProfile(workId, member)
                if (profile == null) {
                    failure(Errors.memberNotFound)
                } else {
                    success(profile)
                }
            }
        }

    fun editWork(workId: UUID, editWork: EditWorkInputModel, user: User) =
        transactionManager.run {
            val workRep = it.workRepository
            val addressRep = it.addressRepository
            val work = workRep.getById(workId)
            if (work == null) {
                failure(Errors.workNotFound)
            } else if (!work.members.checkOwner(user.id)) {
                failure(Errors.notAdmin)
            } else {
                if (work.state == FINISHED) {
                    failure(Errors.workAlreadyFinished)
                } else {
                    val location = addressRep.getLocation(
                        editWork.address.location.parish,
                        editWork.address.location.county,
                        editWork.address.location.district
                    )
                    if (location == null) {
                        failure(Errors.invalidLocation)
                    } else {
                        workRep.editWork(workId, editWork)
                        success(Unit)
                    }
                }
            }
        }

}
