package pt.isel.sitediary.domainmodel.work

import pt.isel.sitediary.domainmodel.user.Member
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
