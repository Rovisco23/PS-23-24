package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.user.Technician
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Invite
import pt.isel.sitediary.domainmodel.work.InviteSimplified
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.domainmodel.work.OpeningTerm
import pt.isel.sitediary.domainmodel.work.Work
import pt.isel.sitediary.domainmodel.work.WorkInput
import pt.isel.sitediary.domainmodel.work.WorkSimplified
import pt.isel.sitediary.domainmodel.work.WorkState
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.OpeningTermInputModel
import pt.isel.sitediary.repository.WorkRepository
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

class JdbiWork(private val handle: Handle) : WorkRepository {
    override fun createWork(work: WorkInput, openingTerm: OpeningTermInputModel, user: User) {
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
            .bind("role", "DONO")
            .execute()
        addCouncilAsMember(work.id, work.address.location)
        val companyId = getCompanyId(openingTerm.company.name, openingTerm.company.num)
        val councilId = getCouncil(work.address.location)
        val tId = insertOpeningTerm(openingTerm, companyId, work.id, councilId)
        insertTechnicians(openingTerm.technicians, tId, work.id)
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
        "select OBRA.id, OBRA.nome, OBRA.tipo, OBRA.descricao, OBRA.estado, OBRA.distrito, OBRA.concelho, " +
                "OBRA.freguesia, OBRA.rua, OBRA.cpostal, ARRAY(SELECT CONCAT(uId, ';', username, ';', MEMBRO.role) " +
                "FROM MEMBRO join UTILIZADOR on uId = id WHERE oId = :id and MEMBRO.pendente = :pending) as membros, " +
                "ARRAY(SELECT CONCAT(nome, ';', role, ';', associacao, ';', numero) from INTERVENIENTE where " +
                "oId = :id) as technicians, ARRAY(SELECT CONCAT(REGISTO.id, ';', author, ';', UTILIZADOR.username, " +
                "';', MEMBRO.role, ';', titulo, ';', estado, ';', TO_CHAR(REGISTO.creation_date, 'YYYY-MM-DD')) " +
                "FROM REGISTO join UTILIZADOR on author = UTILIZADOR.id join MEMBRO on uId = author where " +
                "REGISTO.oId = :id) as log, TERMO_ABERTURA.titular_licenca, TERMO_ABERTURA.predio, " +
                "EMPRESA_CONSTRUCAO.nome as company_name, EMPRESA_CONSTRUCAO.numero as company_num, " +
                "(SELECT COUNT(*) FROM IMAGEM WHERE oId = OBRA.id) as imagens, (SELECT COUNT(*) FROM DOCUMENTO " +
                "WHERE oId = OBRA.id) AS documentos from OBRA join TERMO_ABERTURA on TERMO_ABERTURA.oId = :id " +
                "join EMPRESA_CONSTRUCAO on EMPRESA_CONSTRUCAO.id = TERMO_ABERTURA.empresa_construcao " +
                "where OBRA.id = :id"
    )
        .bind("id", id.toString())
        .bind("pending", false)
        .mapTo(Work::class.java)
        .singleOrNull()

    override fun getWorkList(userId: Int): List<WorkSimplified> = handle.createQuery(
        "select OBRA.id, OBRA.nome, ta.titular_licenca as owner, Obra.tipo, OBRA.descricao, OBRA.estado," +
                " OBRA.freguesia, OBRA.concelho, OBRA.distrito, OBRA.rua, OBRA.cpostal from MEMBRO join" +
                " OBRA on id = oId join TERMO_ABERTURA ta on OBRA.id = ta.oId where uId = :id and " +
                "MEMBRO.pendente = 'false'"
    )
        .bind("id", userId)
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

    override fun getInviteList(userId: Int): List<InviteSimplified> = handle.createQuery(
        "with Table1 as (select Obra.id, Obra.nome as workTitle, Membro.role from Obra join Membro on Obra.id = " +
                "Membro.oId where Membro.uId = :uId and pendente = :pending), Table2 as (select Obra.id, " +
                "Utilizador.username as owner from Membro join Utilizador on Membro.uId = Utilizador.id join Obra " +
                "on Membro.oId = Obra.id where Membro.role = 'DONO' and Membro.uId != :uId and Membro.oId = Obra.id) " +
                "select Table1.id, Table1.workTitle, Table1.role, Table2.owner from Table1 inner join Table2 on " +
                "Table1.id = Table2.id;"
    )
        .bind("uId", userId)
        .bind("pending", true)
        .mapTo(InviteSimplified::class.java)
        .list()

    override fun getInvite(workId: UUID, userId: Int): InviteSimplified? = handle.createQuery(
        "select Obra.id, Obra.nome as workTitle, Membro.role, (select Utilizador.username from Membro join " +
                "Utilizador on Membro.uId = Utilizador.id where Membro.oId = :oId and Membro.role = 'DONO') " +
                "as owner from Obra join Membro on Obra.id = Membro.oId where uid = :uId and Membro.pendente = 'true'"
    )
        .bind("oId", workId.toString())
        .bind("uId", userId)
        .mapTo(InviteSimplified::class.java)
        .singleOrNull()

    override fun acceptInvite(inv: InviteSimplified, user: User) {
        handle.createUpdate(
            "update MEMBRO set pendente = :pendente where uId = :uId and oId = :oId"
        )
            .bind("uId", user.id)
            .bind("oId", inv.workId.toString())
            .bind("pendente", false)
            .execute()

        val tId = handle.createQuery(
            "select id from TERMO_ABERTURA where oId = :oId"
        )
            .bind("oId", inv.workId.toString())
            .mapTo(Int::class.java)
            .single()
        val roleExists = handle.createQuery(
            "select count(*)=0 from Interveniente where role=:role"
        )
            .bind("role", inv.role)
            .mapTo(Boolean::class.java)
            .single()
        if (roleExists) {
            val name = handle.createQuery(
                "select CONCAT(nome,' ',apelido) as nome from UTILIZADOR where id = :uId"
            )
                .bind("uId", user.id)
                .mapTo(String::class.java)
                .single()

            // Adicionar ao Interveniente
            handle.createUpdate(
                "insert into INTERVENIENTE(tId, oId, nome, role, associacao, numero) values(:tId, :oId, :nome, " +
                        ":role, :association,:num)"
            )
                .bind("tId", tId)
                .bind("oId", inv.workId.toString())
                .bind("nome", name)
                .bind("role", inv.role)
                .bind("association", user.association.name)
                .bind("num", user.association.number)
                .execute()
        }

    }

    override fun declineInvite(workId: UUID, userId: Int) {
        handle.createUpdate("delete from MEMBRO where uId = :uId and oId = :oId")
            .bind("uId", userId)
            .bind("oId", workId.toString())
            .execute()
    }

    override fun getWorkListAdmin() = handle.createQuery(
        "select o.id, o.nome, ta.titular_licenca as owner,  o.tipo, o.descricao, o.estado, o.freguesia," +
                " o.concelho, o.distrito, o.rua, o.cpostal from OBRA o join TERMO_ABERTURA ta on ta.oId = o.id"
    )
        .mapTo(WorkSimplified::class.java)
        .list()

    override fun getWorkListCouncil(location: Location) = handle.createQuery(
        "select o.id, o.nome, ta.titular_licenca as owner, o.tipo, o.descricao, o.estado, o.freguesia, o.concelho," +
                " o.distrito, o.rua, o.cpostal from OBRA o join TERMO_ABERTURA ta on ta.oId = o.id " +
                "where freguesia = :parish and concelho = :county and distrito = :district"
    )
        .bind("parish", location.parish)
        .bind("county", location.county)
        .bind("district", location.district)
        .mapTo(WorkSimplified::class.java)
        .list()

    override fun getWorkImage(workId: UUID) = handle.createQuery(
        "select file, name, type from IMAGEM_OBRA where work_id = :id"
    )
        .bind("id", workId.toString())
        .mapTo(FileModel::class.java)
        .singleOrNull()

    override fun checkWorkImageExists(workId: UUID) = handle.createQuery(
        "select work_id from IMAGEM_OBRA where work_id = :id"
    )
        .bind("id", workId.toString())
        .mapTo(UUID::class.java)
        .singleOrNull()

    override fun insertWorkImage(workId: UUID, featuredImage: FileModel) {
        handle.createUpdate("insert into IMAGEM_OBRA(work_id, name, type, file) values (:id, :name, :type, :img)")
            .bind("id", workId.toString())
            .bind("name", featuredImage.fileName)
            .bind("type", featuredImage.contentType)
            .bind("img", featuredImage.file)
            .execute()
    }

    override fun changeWorkImage(workId: UUID, featuredImage: FileModel) {
        handle.createUpdate("update IMAGEM_OBRA set name = :name, type = :type, file = :img where work_id = :id")
            .bind("id", workId.toString())
            .bind("name", featuredImage.fileName)
            .bind("type", featuredImage.contentType)
            .bind("img", featuredImage.file)
            .execute()
    }

    override fun removeWorkImage(workId: UUID) {
        handle.createUpdate("delete from IMAGEM_OBRA where work_id = :id")
            .bind("id", workId.toString())
            .execute()
    }

    override fun finishWork(workId: UUID) {
        handle.createUpdate("update OBRA set estado = :state, data_conclusao = :date where id = :id")
            .bind("id", workId.toString())
            .bind("state", WorkState.FINISHED.toString())
            .bind("date", Timestamp.valueOf(LocalDateTime.now()))
            .execute()
    }

    override fun inviteMember(id: Int, role: String, workId: UUID) {
        handle.createUpdate("insert into MEMBRO(uId, oId, role, pendente) values(:uId, :oId, :role, :pendente)")
            .bind("uId", id)
            .bind("oId", workId.toString())
            .bind("role", role)
            .bind("pendente", true)
            .execute()
    }

    override fun checkRequiredTechnicians(workId: UUID): Boolean = handle.createQuery(
        "select count(*) from INTERVENIENTE where (oId = :id and (role = 'FISCALIZAÇÃO' or role = 'COORDENADOR'))"
    )
        .bind("id", workId.toString())
        .mapTo(Int::class.java)
        .single() == 2

    override fun getNumberOfInvites(id: Int): Int = handle.createQuery(
        "select count(*) from Membro where uId = :uId and pendente = 'True'"
    )
        .bind("uId", id)
        .mapTo(Int::class.java)
        .single()

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

    private fun insertTechnicians(technicians: List<Technician>, tId: Int, workId: UUID) {
        val query = StringBuilder("insert into INTERVENIENTE(tId, oId, nome, role, associacao, numero) values ")
        technicians.forEach {
            query.append(
                "($tId, '$workId', '${it.name}', '${it.role}', '${it.association.name}', " +
                        "${it.association.number}), "
            )
        }
        handle.createUpdate(query.toString().dropLast(2)).execute()
    }
}
