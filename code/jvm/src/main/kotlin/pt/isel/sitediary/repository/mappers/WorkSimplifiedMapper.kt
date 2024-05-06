package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import pt.isel.sitediary.domainmodel.work.WorkState
import pt.isel.sitediary.domainmodel.work.WorkType
import java.sql.ResultSet
import java.util.*

class WorkSimplifiedMapper : RowMapper<WorkSimplified> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): WorkSimplified? = if (rs != null) {
        WorkSimplified(
            id = UUID.fromString(rs.getString("id")),
            name = rs.getString("nome"),
            description = rs.getString("descricao"),
            type = WorkType.fromString(rs.getString("tipo")) ?: WorkType.RESIDENCIAL,
            state = WorkState.fromString(rs.getString("estado")) ?: WorkState.IN_PROGRESS,
            address = Address(
                location = Location(
                    rs.getString("distrito"),
                    rs.getString("concelho"),
                    rs.getString("freguesia")
                ),
                rs.getString("rua"),
                rs.getString("cpostal")
            )
        )
    } else null
}