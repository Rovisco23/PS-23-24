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
    val type: String,
    val state: String,
    val address: Address,
)

data class BuildingCompany(
    val name: String,
    val num: Int
)

data class OpeningTerm(
    //val image: Image,
    val name: String,
    val type: WorkType,
    val description: String?,
    val holder: String,
    val director: String,
    val company: BuildingCompany,
    val building: String,
    val address: Address,
    val technicians: List<Technician>
) {
    fun checkParams() =
        name.isBlank()  || holder.isBlank() || director.isBlank() || company.name.isBlank() || company.num <= 0 ||
                building.isBlank() || address.location.parish.isBlank() || address.street.isBlank() ||
                address.postalCode.isBlank() || address.location.county.isBlank()
}

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
            "REABILITACAO" -> REABILITACAO
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