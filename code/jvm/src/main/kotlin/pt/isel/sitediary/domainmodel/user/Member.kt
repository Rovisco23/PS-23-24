package pt.isel.sitediary.domainmodel.user

data class Member(
    val id: Int,
    val name: String,
    val role: String,
    val phone: String
)

fun List<Member>.containsMemberById(userId: Int) = this.any { it.id == userId }