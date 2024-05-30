package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.work.InviteSimplified
import java.sql.ResultSet
import java.util.*

class InviteSimplifiedModel  : RowMapper<InviteSimplified> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): InviteSimplified? = if (rs != null) {
        InviteSimplified(
            UUID.fromString(rs.getString("id")),
            UUID.fromString(rs.getString("workId")),
            rs.getString("workTitle"),
            rs.getString("role"),
            rs.getString("admin")
        )
    } else null
}