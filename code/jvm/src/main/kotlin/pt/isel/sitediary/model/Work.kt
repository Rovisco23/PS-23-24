package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Address
import pt.isel.sitediary.domainmodel.work.BuildingCompany
import pt.isel.sitediary.domainmodel.work.OpeningTerm
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
    val company: BuildingCompany,
    val building: String,
    val address: Address,
) {
    fun toOpeningTerm() = OpeningTerm(
        name = name,
        type = type,
        description = description,
        holder = holder,
        director = director,
        company = company,
        building = building,
        address = address,
        technicians = emptyList()
    )
}

data class MemberInputModel(
    val email: String,
    val role: String
)