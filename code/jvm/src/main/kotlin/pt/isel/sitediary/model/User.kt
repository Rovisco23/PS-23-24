package pt.isel.sitediary.model

import pt.isel.sitediary.domainmodel.work.Location


data class SignUpInputModel(
    val email: String,
    val role: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String?,
    val parish: String,
    val county: String
)

data class LoginInputModel(
    val user: String,
    val password: String
)

data class EditProfileInputModel(
    val username: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val parish: String?,
    val county: String?
)

data class GetUserModel(
    val id: Int,
    val username: String,
    val email: String,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val role: String,
    val location: Location
)

data class LocationModel(
    val district: String,
    val county: String,
    val parish: String
)

data class TokenOutputModel(val userId: Int, val username: String, val token: String)
