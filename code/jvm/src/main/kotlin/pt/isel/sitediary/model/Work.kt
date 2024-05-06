package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Address
import java.util.*

data class ListOfWorksOutputModel(val list: List<WorkOutputModel>)

data class WorkOutputModel(
    val id: UUID,
    val name: String,
    val description: String,
    val address: Address
)