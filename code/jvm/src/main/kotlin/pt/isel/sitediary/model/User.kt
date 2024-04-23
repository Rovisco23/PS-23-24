package pt.isel.sitediary.model

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
