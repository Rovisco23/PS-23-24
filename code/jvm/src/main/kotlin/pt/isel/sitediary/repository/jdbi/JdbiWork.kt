package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import pt.isel.sitediary.model.OpeningTermInputModel
import pt.isel.sitediary.repository.WorkRepository
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class JdbiWork(private val handle: Handle) : WorkRepository {
    override fun createWork(work: Work, openingTerm: OpeningTermInputModel, user: User) {
        handle.createUpdate(
            "insert into OBRA(id, nome, tipo, descricao, estado, freguesia, concelho, distrito, rua, cpostal)" +
                    "values (:id, :nome, :tipo, :descricao, :estado, :freguesia, :concelho, :distrito, :rua, :cpostal)"
        )
            .bind("id", work.id)
            .bind("nome", work.name)
            .bind("tipo", work.type.toString())
            .bind("descricao", work.description)
            .bind("estado", work.state.toString())
            .bind("freguesia", work.address.location.parish)
            .bind("concelho", work.address.location.county)
            .bind("distrito", work.address.location.district)
            .bind("rua", work.address.street)
            .bind("cpostal", work.address.postalCode)
            .execute()
        handle.createUpdate(
            "insert into MEMBRO(oId, uId, role) values (:id_obra, :id_utilizador, :role)"
        )
            .bind("id_obra", work.id)
            .bind("id_utilizador", user.id)
            .bind("role", "ADMIN")
            .execute()
        val companyId = getCompanyId(openingTerm.company.name, openingTerm.company.num)
        val councilId = getCouncil(work.address.location)
        val termId = insertOpeningTerm(openingTerm, companyId, work.id, councilId)
        /** Exemplo
        handle.createUpdate(
            "insert into TECNICO(nif,tId, oId, nome, tipo, associacao, numero) " +
                    "values (:nif, :tId, :oId, :nome, :tipo, :associacao, :numero)"
        )
            .bind("nif", user.nif)
            .bind("tId", termId)
            .bind("oId", work.id)
            .bind("nome", work.name)
            .bind("tipo", "Por Adicionar mas virá no openingTerm.fiscalization.type")
            .bind("associacao", "Por Adicionar mas virá no openingTerm.fiscalization.association")
            .bind("numero", 0) // Por Adicionar mas virá no openingTerm.fiscalization.number
            .execute()
        */
    }

    override fun getById(id: UUID): Work? = handle.createQuery(
        "select *, ARRAY(SELECT CONCAT(uId, ';', username, ';', MEMBRO.role) FROM MEMBRO " +
                "join UTILIZADOR on uId = id WHERE oId = :id) as membros, ARRAY(" +
                "SELECT CONCAT(REGISTO.id, ';', author, ';', UTILIZADOR.username, ';', MEMBRO.role, ';', " +
                "titulo, ';', estado, ';', TO_CHAR(REGISTO.creation_date, 'YYYY-MM-DD')) FROM REGISTO " +
                "join UTILIZADOR on author = UTILIZADOR.id join MEMBRO on uId = author where REGISTO.oId = :id) " +
                "as log from OBRA where id = :id"
    )
        .bind("id", id.toString())
        .mapTo(Work::class.java)
        .singleOrNull()

    override fun getWorkList(skip: Int, userId: Int): List<WorkSimplified> = handle.createQuery(
        "select OBRA.id, OBRA.nome, Obra.tipo, OBRA.descricao, OBRA.estado, OBRA.freguesia, OBRA.concelho, " +
                "OBRA.distrito, OBRA.rua, OBRA.cpostal from MEMBRO join OBRA on id = oId where uId = :id" +
                " OFFSET :skip LIMIT 6"
    )
        .bind("id", userId)
        .bind("skip", skip)
        .mapTo(WorkSimplified::class.java)
        .list()

    override fun getOpeningTerm(workId: UUID): OpeningTerm = handle.createQuery(
        "select OBRA.nome, OBRA.tipo, TERMO_ABERTURA.titular_licenca, EMPRESA_CONSTRUCAO.nome as company_name, " +
                "EMPRESA_CONSTRUCAO.numero as company_num, TERMO_ABERTURA.predio, ARRAY(select CONCAT(nif, nome, " +
                "tipo, associacao, numero) from TECNICO where TECNICO.oId = :id) as tecnicos from TERMO_ABERTURA " +
                "join EMPRESA_CONSTRUCAO on EMPRESA_CONSTRUCAO.id = TERMO_ABERTURA.empresa_construcao " +
                "join OBRA on OBRA.id = TERMO_ABERTURA.oId where TERMO_ABERTURA.oId = :id"
    )
        .bind("id", workId)
        .mapTo(OpeningTerm::class.java)
        .single()

    private fun getCouncil(location: Location) =
        handle.createQuery(
            "select id from Localidade where freguesia = :freguesia and concelho = " +
                    ":concelho and distrito = :distrito"
        )
            .bind("freguesia", location.parish)
            .bind("concelho", location.county)
            .bind("distrito", location.district)
            .mapTo(Int::class.java)
            .single()

    private fun getCompanyId(name: String, number: Int) = handle.createQuery(
        "select id from EMPRESA_CONSTRUCAO where nome = :nome and numero = :numero"
    )
        .bind("nome", name)
        .bind("numero", number)
        .mapTo(Int::class.java)
        .singleOrNull() ?: handle.createUpdate(
        "insert into EMPRESA_CONSTRUCAO(nome, numero)" +
                "values (:nome, :numero)"
    )
        .bind("nome", name)
        .bind("numero", number)
        .executeAndReturnGeneratedKeys()
        .mapTo(Int::class.java)
        .one()

    private fun insertOpeningTerm(
        openingTerm: OpeningTermInputModel,
        companyId: Int,
        workId: UUID,
        councilId: Int
    ): Int = handle.createUpdate(
            "insert into TERMO_ABERTURA(oId, inicio, camara, titular_licença, empresa_construção, predio)" +
                    "values (:oId, :inicio, :camara, :titular_licença, :empresa_construção, :predio)"
        )
            .bind("oId", workId)
            .bind("inicio", Timestamp.valueOf(LocalDateTime.now()))
            .bind("camara", councilId)
            .bind("titular_licença", openingTerm.holder)
            .bind("empresa_construção", companyId)
            .bind("predio", openingTerm.building)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
}
