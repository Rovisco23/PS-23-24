package pt.isel.sitediary.domainmodel.user

data class Member(
    val id: Int,
    val name: String,
    val role: String
)

fun List<Member>.containsMemberById(userId: Int) = this.any { it.id == userId }

fun List<Member>.checkAdmin(userId: Int) = this.any { it.id == userId && it.role == "ADMIN"}

fun List<Member>.checkTechnician(userId: Int) = this.any { it.id == userId && it.role == "TÉCNICO"}