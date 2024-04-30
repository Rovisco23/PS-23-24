package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.repository.WorkRepository
import java.util.*

class JdbiWork (private val handle: Handle): WorkRepository {
    override fun createWork(): UUID {
        TODO("Not yet implemented")
    }

    override fun getById(id: UUID): Work? {
        TODO("Not yet implemented")
    }

    override fun getWorkList(skip: Int, userId: Int): List<Work> {
        TODO("Not yet implemented")
    }

}