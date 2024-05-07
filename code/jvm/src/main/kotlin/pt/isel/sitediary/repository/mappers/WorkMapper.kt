package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.user.Member
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkState
import pt.isel.sitediary.domainmodel.work.WorkType
import java.sql.ResultSet
import java.util.*

class WorkMapper : RowMapper<Work> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Work? = if (rs != null) {
        Work(
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
            ),
            members = rs.getString("membros").removeSurrounding("{", "}").split(",")
                .map {
                    val aux = it.split(";")
                    Member(
                        id = aux[0].toInt(),
                        name = aux[1],
                        role = aux[2]
                    )
                }
        )
    } else null
}
