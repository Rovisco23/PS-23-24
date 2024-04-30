package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.model.LocationModel
import java.sql.ResultSet

class LocationMapper : RowMapper<LocationModel> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): LocationModel? = if (rs != null) {
        LocationModel(
            rs.getString("distrito"),
            rs.getString("concelho"),
            rs.getString("freguesia")
            )
    } else null

}