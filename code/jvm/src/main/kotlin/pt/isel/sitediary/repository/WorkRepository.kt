package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import java.util.*

interface WorkRepository {
    fun createWork(work: Work, openingTerm: OpeningTerm, userId: Int)
    fun getById(id: UUID): Work?
    fun getWorkList(skip: Int, userId: Int): List<WorkSimplified>
}