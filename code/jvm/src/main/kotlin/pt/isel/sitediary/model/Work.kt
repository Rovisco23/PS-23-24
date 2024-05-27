package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.Association
import pt.isel.sitediary.domainmodel.work.ConstructionCompany
import pt.isel.sitediary.domainmodel.work.WorkType
import java.util.*

data class ListOfWorksOutputModel(val list: List<WorkOutputModel>)

data class WorkOutputModel(
    val id: UUID,
    val name: String,
    val description: String,
    val address: Address
)

data class OpeningTermInputModel(
    val name: String,
    val type: WorkType,
    val description: String?,
    val holder: String,
    val director: String,
    val company: ConstructionCompany,
    val building: String,
    val address: Address,
) {
    fun checkParams() =
        name.isBlank() || holder.isBlank() || director.isBlank() || company.name.isBlank() || company.num <= 0 ||
                building.isBlank() || address.location.parish.isBlank() || address.street.isBlank() ||
                address.postalCode.isBlank() || address.location.county.isBlank()
}

data class MemberInputModel(
    val email: String,
    val role: String
)

data class InviteResponseModel(
    val id: UUID,
    val workId: UUID,
    val accepted: Boolean,
    val role: String,
    val association: Association?
)