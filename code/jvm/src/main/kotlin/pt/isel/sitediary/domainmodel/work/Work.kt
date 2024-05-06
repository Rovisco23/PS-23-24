package pt.isel.sitediary.domainmodel.work

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
    val members: List<Member>
)

data class WorkSimplified(
    val id: UUID,
    val name: String,
    val description: String,
    val type: WorkType,
    val state: WorkState,
    val address: Address,
)

data class BuildingCompany(
    val name: String,
    val num: Int
) {
    fun isBlank() = name.isBlank() || num <= 0
}

data class OpeningTerm(
    //val image: Image,
    val name: String,
    val type: WorkType,
    val description: String?,
    val holder: String,
    val director: String,
    val company: BuildingCompany,
    val building: String,
    val parish: String,
    val county: String,
    val street: String,
    val postalCode: String,
    val technicians: List<Technician>
) {
    fun checkParams() =
        name.isBlank() || description.isNullOrBlank() || holder.isBlank() || director.isBlank() || company.isBlank() ||
                building.isBlank() || parish.isBlank() || street.isBlank() || postalCode.isBlank() || county.isBlank()
}

enum class WorkType {
    RESIDENCIAL,
    COMERCIAL,
    INDUSTRIAL,
    INFRAESTRUTURA,
    INSTITUCIONAL,
    REABILITACAO,
    ESTRUTURA_ESPECIAL,
    OBRA_DE_ARTE,
    HABITACAO,
    EDIFICIOS_ESPECIAL;

    override fun toString() = when (this) {
        RESIDENCIAL -> "RESIDENCIAL"
        COMERCIAL -> "COMERCIAL"
        INDUSTRIAL -> "INDUSTRIAL"
        INFRAESTRUTURA -> "INFRAESTRUTURA"
        INSTITUCIONAL -> "INSTITUCIONAL"
        REABILITACAO -> "REABILITAÇÃO"
        ESTRUTURA_ESPECIAL -> "ESTRUTURA ESPECIAL"
        OBRA_DE_ARTE -> "OBRA DE ARTE"
        HABITACAO -> "HABITAÇÃO"
        EDIFICIOS_ESPECIAL -> "EDIFICIOS ESPECIAL"
    }

    companion object {
        fun fromString(type: String) = when (type) {
            "RESIDENCIAL" -> RESIDENCIAL
            "COMERCIAL" -> COMERCIAL
            "INDUSTRIAL" -> INDUSTRIAL
            "INFRAESTRUTURA" -> INFRAESTRUTURA
            "INSTITUCIONAL" -> INSTITUCIONAL
            "REABILITACAO" -> REABILITACAO
            "ESTRUTURA ESPECIAL" -> ESTRUTURA_ESPECIAL
            "OBRA DE ARTE" -> OBRA_DE_ARTE
            "HABITAÇÃO" -> HABITACAO
            "EDIFICIOS ESPECIAL" -> EDIFICIOS_ESPECIAL
            else -> null
        }
    }
}

enum class WorkState {
    IN_PROGRESS,
    FINISHED,
    CANCELED,
    PAUSED;

    override fun toString() = when (this) {
        IN_PROGRESS -> "EM PROGRESSO"
        FINISHED -> "TERMINADA"
        CANCELED -> "CANCELADA"
        PAUSED -> "EM PAUSA"
    }

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