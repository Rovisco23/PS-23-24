package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import pt.isel.sitediary.model.OpeningTermInputModel
import java.util.*

interface WorkRepository {
    fun createWork(work: Work, openingTerm: OpeningTermInputModel, user: User)
    fun getById(id: UUID): Work?
    fun getWorkList(skip: Int, userId: Int): List<WorkSimplified>
    fun getOpeningTerm(workId: UUID): OpeningTerm
}