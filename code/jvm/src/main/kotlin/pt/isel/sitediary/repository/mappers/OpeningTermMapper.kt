package pt.isel.sitediary.repository.mappers


import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.sitediary.domainmodel.user.Technician
import pt.isel.sitediary.domainmodel.work.ConstructionCompany
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.WorkType
import java.sql.ResultSet

class OpeningTermMapper : RowMapper<OpeningTerm> {

    override fun map(rs: ResultSet?, ctx: StatementContext?): OpeningTerm? = if (rs != null) {
        OpeningTerm(
            name = rs.getString("nome"),
            type = WorkType.fromString(rs.getString("tipo"))!!,
            licenseHolder = rs.getString("titular_licenca"),
            constructionCompany = ConstructionCompany(
                name = rs.getString("nome_empresa"),
                num = rs.getInt("num_empresa")
            ),
            building = rs.getString("predio"),
            technicians = rs.getString("tecnicos").removeSurrounding("{", "}").split(",")
                .map {
                    val aux = it.split(";")
                    Technician(
                        nif = aux[0].toInt(),
                        name = aux[1],
                        role = aux[2],
                        association = aux[3],
                        associationNum = aux[4].toInt()
                    )
                },
        )
    } else null
}