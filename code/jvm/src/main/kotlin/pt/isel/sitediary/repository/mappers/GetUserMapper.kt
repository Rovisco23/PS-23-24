package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.utils.Location
import java.sql.ResultSet

class GetUserMapper : RowMapper<GetUserModel> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): GetUserModel? = if (rs != null) {
        GetUserModel(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getString("telefone"),
            rs.getString("role"),
            Location(
                rs.getString("distrito"),
                rs.getString("concelho"),
                rs.getString("freguesia")
            )
        )
    } else null

}
