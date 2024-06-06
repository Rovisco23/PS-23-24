package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.work.Author
import pt.isel.sitediary.domainmodel.work.LogEntry
import java.sql.Date
import java.sql.ResultSet
import java.util.*

class LogEntryMapper : RowMapper<LogEntry> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): LogEntry? = if (rs != null) {
        val modificationDate = rs.getTimestamp("last_modification_date")
        LogEntry(
            id = rs.getInt("id"),
            workId = UUID.fromString(rs.getString("oId")),
            title = rs.getString("titulo"),
            content = rs.getString("texto"),
            editable = rs.getBoolean("editable"),
            createdAt = Date.from(rs.getTimestamp("creation_date").toInstant()),
            lastModifiedAt = if (modificationDate != null) Date.from(modificationDate.toInstant()) else null,
            author = Author(
                id = rs.getInt("author"),
                name = rs.getString("username"),
                role = rs.getString("role")
            )
        )
    } else null
}
