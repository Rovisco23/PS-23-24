package pt.isel.sitediary.repository.jdbi

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.work.LogEntry
import pt.isel.sitediary.model.LogInputModel
import pt.isel.sitediary.repository.LogRepository
import java.sql.Timestamp
import java.util.*

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

    override fun getById(id: Int): LogEntry? = handle.createQuery(
        "select REGISTO.id, titulo, REGISTO.oid, texto, estado, creation_date, last_modification_date, author, " +
                "username, MEMBRO.role from REGISTO join UTILIZADOR on UTILIZADOR.id = author " +
                "join MEMBRO on MEMBRO.uid = author where Registo.id = :id"
    )
        .bind("id", id)
        .mapTo(LogEntry::class.java)
        .singleOrNull()

    override fun checkUserAccess(workId: UUID, userId: Int): Boolean = handle.createQuery(
        "select count(*) from MEMBRO where oId = :oId and uId = :uId"
    )
        .bind("oId", workId.toString())
        .bind("uId", userId)
        .mapTo(Int::class.java)
        .single() == 1
}