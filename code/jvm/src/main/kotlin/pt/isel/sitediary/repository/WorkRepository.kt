package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.*
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.InviteResponseModel
import pt.isel.sitediary.model.OpeningTermInputModel
import java.util.*

interface WorkRepository {
    fun createWork(work: Work, openingTerm: OpeningTermInputModel, user: User)
    fun getById(id: UUID): Work?
    fun getWorkList(skip: Int, userId: Int): List<WorkSimplified>
    fun getOpeningTerm(workId: UUID): OpeningTerm
    fun inviteMembers(invites: List<Invite>)
    fun checkInvite(workId: UUID, email: String): Boolean
    fun getInviteList(email: String): List<InviteSimplified>
    fun getInvite(id: UUID, email: String): InviteSimplified?
    fun acceptInvite(inv: InviteResponseModel, user: GetUserModel)
    fun declineInvite(id: UUID)
}