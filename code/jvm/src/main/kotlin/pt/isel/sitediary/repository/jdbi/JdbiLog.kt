package pt.isel.sitediary.repository.jdbi

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.jdbi.v3.core.Handle
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.LogRepository
import java.sql.Timestamp

class JdbiLog(private val handle: Handle) : LogRepository {

    override fun createLog(log: LogInputModel, createdAt: Instant, lastUsedAt: Instant, user: Int): Int =
        handle.createUpdate(
        "insert into registo(oId, texto, estado, creation_date, last_modification_date, author)" +
                "values (:workId, :description, :state, :createdAt, :lastUsedAt, :user)"
    )
            .bind("workId", log.workId)
            .bind("description", log.description)
            .bind("state", "EDITÁVEL")
            .bind("createdAt", Timestamp.from(createdAt.toJavaInstant()))
            .bind("lastUsedAt", Timestamp.from(lastUsedAt.toJavaInstant()))
            .bind("user", user)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

}