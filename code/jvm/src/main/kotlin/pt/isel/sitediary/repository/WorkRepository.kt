package pt.isel.sitediary.repository

import pt.isel.sitediary.utils.Work
import java.util.*

interface WorkRepository {
    fun createWork() : UUID
    fun getById(id: UUID) : Work?
    fun getWorkList(skip: Int, userId: Int) : List<Work>
}