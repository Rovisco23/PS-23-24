package pt.isel.sitediary.repository.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.user.Member
import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Author
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.LogEntrySimplified
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkState
import pt.isel.sitediary.domainmodel.work.WorkType
import java.sql.Date
import java.sql.ResultSet
import java.util.*

class WorkMapper : RowMapper<Work> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): Work? = if (rs != null) {
        val log = rs.getString("log").removeSurrounding("{", "}")
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
                },
            log = if (log.isEmpty()) emptyList() else {
                log.split(",")
                    .map {
                        val x = '"'.toString()
                        val aux = it.removeSurrounding(x, x).split(";")
                        LogEntrySimplified(
                            id = aux[0].toInt(),
                            author = Author(
                                id = aux[1].toInt(),
                                name = aux[2],
                                role = aux[3]
                            ),
                            title = aux[4],
                            state = aux[5],
                            createdAt = Date.valueOf(aux[6])
                        )
                    }
            }
        )
    } else null
}
