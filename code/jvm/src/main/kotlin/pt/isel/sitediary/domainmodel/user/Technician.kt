package pt.isel.sitediary.domainmodel.user

data class Technician(
    val name: String,
    val role: String,
    val association: String,
    val associationNum: Int
)