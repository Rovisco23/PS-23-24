package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Invite
import pt.isel.sitediary.domainmodel.work.InviteSimplified
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.InviteResponseModel
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
        addCouncilAsMember(work.id, work.address.location)
        val companyId = getCompanyId(openingTerm.company.name, openingTerm.company.num)
        val councilId = getCouncil(work.address.location)
        insertOpeningTerm(openingTerm, companyId, work.id, councilId)
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
                "EMPRESA_CONSTRUCAO.numero as company_num, TERMO_ABERTURA.predio, ARRAY(select CONCAT(nif, ';', " +
                "username, ';', MEMBRO.role, ';', associacao_nome, ';', associacao_numero) from MEMBRO join " +
                "UTILIZADOR on uId = UTILIZADOR.id where MEMBRO.oId = :id and MEMBRO.role != 'ADMIN' and " +
                "MEMBRO.role != 'MEMBRO' and MEMBRO.role != 'ESPECTADOR') as tecnicos from TERMO_ABERTURA join " +
                "EMPRESA_CONSTRUCAO on EMPRESA_CONSTRUCAO.id = TERMO_ABERTURA.empresa_construcao join OBRA on " +
                "OBRA.id = TERMO_ABERTURA.oId where TERMO_ABERTURA.oId = :id"
    )
        .bind("id", workId.toString())
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
        "insert into TERMO_ABERTURA(oId, inicio, camara, titular_licenca, empresa_construcao, predio)" +
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

    override fun inviteMembers(invites: List<Invite>) {
        val query = StringBuilder("insert into CONVITE(id, email, role, oId) values ")
        invites.forEach {
            query.append("('${it.id}', '${it.email}', '${it.role}', '${it.workId}'), ")
        }
        handle.createUpdate(query.toString().dropLast(2)).execute()
    }

    override fun checkInvite(workId: UUID, email: String): Boolean = handle.createQuery(
        "select count(*) from CONVITE where oId = :oId and email = :email"
    )
        .bind("oId", workId.toString())
        .bind("email", email)
        .mapTo(Int::class.java)
        .single() == 1

    private fun addCouncilAsMember(workId: UUID, location: Location) {
        val councilId = handle.createQuery(
            "select id from UTILIZADOR where freguesia = :parish and " +
                    "concelho = :county and distrito = :district and role='CÂMARA'"
        )
            .bind("parish", location.parish)
            .bind("county", location.county)
            .bind("district", location.district)
            .mapTo(Int::class.java)
            .singleOrNull()
        if (councilId != null) {
            handle.createUpdate("insert into MEMBRO(uId, oId, role) values(:uId, :oId, :role)")
                .bind("uId", councilId)
                .bind("oId", workId.toString())
                .bind("role", "ESPECTADOR")
        }
    }

    override fun getInviteList(email: String): List<InviteSimplified> = handle.createQuery(
        "select c.id as id, o.id as workId, o.nome as workTitle, u.username as admin, c.role as role from CONVITE c " +
                "join OBRA o on o.id = c.oId join MEMBRO m on o.id = m.oId join UTILIZADOR u on m.uId = u.id " +
                "where c.email = :email and m.role = 'ADMIN'"
    )
        .bind("email", email)
        .mapTo(InviteSimplified::class.java)
        .list()

    override fun getInvite(id: UUID, email: String): InviteSimplified? = handle.createQuery(
        "select c.id as id, o.id as workId, o.nome as workTitle, u.username as admin, c.role as role from CONVITE c " +
                "join OBRA o on o.id = c.oId join MEMBRO m on o.id = m.oId join UTILIZADOR u on m.uId = u.id " +
                "where c.id = :id and c.email = :email and m.role = 'ADMIN'"
    )
        .bind("id", id.toString())
        .bind("email", email)
        .mapTo(InviteSimplified::class.java)
        .singleOrNull()

    override fun acceptInvite(inv: InviteResponseModel, user: GetUserModel) {
        handle.createUpdate(
            "insert into MEMBRO(uId, oId, role) values(:uId, :oId, :role)"
        )
            .bind("uId", user.id)
            .bind("oId", inv.workId.toString())
            .bind("role", inv.role)
            .execute()
        handle.createUpdate("delete from CONVITE where id = :id")
            .bind("id", inv.id.toString())
            .execute()
    }

    override fun declineInvite(id: UUID) {
        handle.createUpdate("delete from CONVITE where id = :id")
            .bind("id", id.toString())
            .execute()
    }

    override fun getWorkListAdmin(skip: Int) = handle.createQuery(
        "select id, nome, tipo, descricao, estado, freguesia, concelho, distrito, rua, cpostal from OBRA " +
                "OFFSET :skip LIMIT 6"
    )
        .bind("skip", skip)
        .mapTo(WorkSimplified::class.java)
        .list()

    override fun getWorkListCouncil(skip: Int, location: Location) = handle.createQuery(
        "select id, nome, tipo, descricao, estado, freguesia, concelho, distrito, rua, cpostal from OBRA " +
                "where freguesia = :parish and concelho = :county and distrito = :district OFFSET :skip LIMIT 6"
    )
        .bind("parish", location.parish)
        .bind("county", location.county)
        .bind("district", location.district)
        .bind("skip", skip)
        .mapTo(WorkSimplified::class.java)
        .list()
}
