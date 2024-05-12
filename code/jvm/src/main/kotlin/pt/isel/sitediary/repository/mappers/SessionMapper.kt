package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.model.SessionModel
import java.sql.ResultSet

class SessionMapper : RowMapper<SessionModel> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): SessionModel? = if (rs != null) {
        SessionModel(
            rs.getInt("uid"),
            rs.getString("token_validation"),
            rs.getLong("created_at"),
            rs.getLong("last_used_at")
        )
    } else null

}