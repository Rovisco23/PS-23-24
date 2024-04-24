package pt.isel.sitediary.model

import pt.isel.sitediary.utils.Location

data class SignUpInputModel(
    val email: String,
    val role: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val parish: String
)

data class LoginInputModel(
    val user: String,
    val password: String
)

data class GetUserModel(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
    val location: Location
)
