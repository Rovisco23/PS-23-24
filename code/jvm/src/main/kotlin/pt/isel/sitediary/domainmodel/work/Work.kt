package pt.isel.sitediary.domainmodel.work

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import pt.isel.sitediary.domainmodel.user.Member
import pt.isel.sitediary.domainmodel.user.Technician
import java.util.*

data class Work(
    val id: UUID,
    val name: String,
    val description: String,
    val type: WorkType,
    val state: WorkState,
    val address: Address,
    val members: List<Member>,
    val log: List<LogEntrySimplified>
) {
    fun createInvites(invite: Invite) {
        val mail = SimpleEmail()
        mail.hostName = "smtp.googlemail.com"
        mail.setSmtpPort(465)
        mail.setAuthenticator(DefaultAuthenticator("ricardorovisco23@gmail.com", "jkqi ailn dgfa oyzt"))
        mail.isSSLOnConnect = true
        mail.setFrom("ricardorovisco23@gmail.com", "SiteDiary")
        mail.addTo(invite.email)
        mail.subject = "Convite para a obra $name"
        val acceptLink = "http://localhost:4200/invites/$id"
        val role = if (invite.role != "MEMBRO" && invite.role != "VIEWER") "Técnico de ${invite.role}" else invite.role
        mail.setMsg(
            "Olá\n\nFoi convidado para a obra $name para participar como ${role}.\n\n" +
                    "Clique no link para aceitar o convite: $acceptLink\n\n" +
                    "Cumprimentos,\nA equipa da SiteDiary"
        )
        mail.send()
    }
}

data class LogEntry(
    val id: Int,
    val workId: UUID,
    val author: Author,
    val title: String,
    val content: String,
    val state: String,
    val createdAt: Date,
    val lastModifiedAt: Date?
)

data class Author(
    val id: Int,
    val name: String,
    val role: String
)

data class LogEntrySimplified(
    val id: Int,
    val author: Author,
    val title: String,
    val state: String,
    val createdAt: Date
)

data class WorkSimplified(
    val id: UUID,
    val name: String,
    val description: String,
    val type: String,
    val state: String,
    val address: Address,
)

data class ConstructionCompany(
    val name: String,
    val num: Int
)

data class Association(
    val name: String,
    val number: Int
)

data class OpeningTerm(
    val name: String, //
    val type: WorkType, //
    val licenseHolder: String,
    val technicians: List<Technician>,
    val constructionCompany: ConstructionCompany, //
    val building: String //
)

enum class WorkType(val description: String) {
    RESIDENCIAL("RESIDENCIAL"),
    COMERCIAL("COMERCIAL"),
    INDUSTRIAL("INDUSTRIAL"),
    INFRAESTRUTURA("INFRAESTRUTURA"),
    INSTITUCIONAL("INSTITUCIONAL"),
    REABILITACAO("REABILITAÇÃO"),
    ESTRUTURA_ESPECIAL("ESTRUTURA ESPECIAL"),
    OBRA_DE_ARTE("OBRA DE ARTE"),
    HABITACAO("HABITAÇÃO"),
    EDIFICIOS_ESPECIAL("EDIFICIOS ESPECIAL");

    override fun toString() = description

    companion object {
        fun fromString(type: String) = when (type) {
            "RESIDENCIAL" -> RESIDENCIAL
            "COMERCIAL" -> COMERCIAL
            "INDUSTRIAL" -> INDUSTRIAL
            "INFRAESTRUTURA" -> INFRAESTRUTURA
            "INSTITUCIONAL" -> INSTITUCIONAL
            "REABILITAÇÃO" -> REABILITACAO
            "ESTRUTURA ESPECIAL" -> ESTRUTURA_ESPECIAL
            "OBRA DE ARTE" -> OBRA_DE_ARTE
            "HABITAÇÃO" -> HABITACAO
            "EDIFICIOS ESPECIAL" -> EDIFICIOS_ESPECIAL
            else -> null
        }
    }
}

enum class WorkState(val description: String) {
    IN_PROGRESS("EM PROGRESSO"),
    FINISHED("TERMINADA"),
    CANCELED("CANCELADA"),
    PAUSED("EM PAUSA");

    override fun toString() = description

    companion object {
        fun fromString(state: String) = when (state) {
            "EM PROGRESSO" -> IN_PROGRESS
            "TERMINADA" -> FINISHED
            "CANCELADA" -> CANCELED
            "EM PAUSA" -> PAUSED
            else -> null
        }
    }
}

data class Invite (val id: UUID, val email: String, val role: String, val workId: UUID)

data class InviteSimplified(val id: UUID, val workId: UUID, val workTitle: String, val role: String, val admin: String)