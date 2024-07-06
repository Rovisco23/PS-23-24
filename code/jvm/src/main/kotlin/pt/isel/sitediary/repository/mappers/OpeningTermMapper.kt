package pt.isel.sitediary.repository.mappers


import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.user.Technician
import pt.isel.sitediary.domainmodel.work.Association
import pt.isel.sitediary.domainmodel.work.ConstructionCompany
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.WorkType
import java.sql.ResultSet

class OpeningTermMapper : RowMapper<OpeningTerm> {
    override fun map(rs: ResultSet?, ctx: StatementContext?): OpeningTerm? = if (rs != null) {
        val technicians = rs.getString("tecnicos").removeSurrounding("{", "}").split(",")
        OpeningTerm(
            name = rs.getString("nome"),
            type = WorkType.fromString(rs.getString("tipo"))!!,
            licenseHolder = rs.getString("titular_licenca"),
            constructionCompany = ConstructionCompany(
                name = rs.getString("company_name"),
                num = rs.getInt("company_num")
            ),
            building = rs.getString("predio"),
            technicians = if(technicians.isEmpty()) emptyList() else technicians
                .map {
                    val x = '"'.toString()
                    val aux = it.removeSurrounding(x, x).split(";")
                    Technician(
                        name = aux[1],
                        email = aux[0],
                        role = aux[2],
                        association = Association(
                            name = aux[3],
                            number = aux[4].toInt()
                        )
                    )
                },
        )
    } else null
}