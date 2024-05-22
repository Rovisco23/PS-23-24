package pt.isel.sitediary.repository.jdbi

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.jdbi.v3.core.Handle
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.LogRepository
import java.sql.Timestamp

class JdbiLog(private val handle: Handle) : LogRepository {

    override fun createLog(log: LogInputModel, createdAt: Instant, author: Int): Int =
        handle.createUpdate(
        "insert into registo(oId, titulo, texto, estado, creation_date, author)" +
                "values (:workId, :title, :description, :state, :createdAt, :author)"
    )
            .bind("workId", log.workId)
            .bind("title", log.title)
            .bind("description", log.description)
            .bind("state", "EDITÁVEL")
            .bind("createdAt", Timestamp.from(createdAt.toJavaInstant()))
            .bind("author", author)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
}