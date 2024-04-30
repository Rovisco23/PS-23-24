package pt.isel.sitediary.domainmodel.work

import pt.isel.sitediary.domainmodel.user.Member
import java.util.*

data class Work(
    val id: UUID,
    val name: String,
    val description: String,
    val address: Address,
    val members: List<Member>
)
