package pt.isel.sitediary.model

import kotlinx.datetime.Instant
import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenValidationInfo
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Association
import pt.isel.sitediary.domainmodel.work.Location


data class SignUpInputModel(
    val email: String,
    val role: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val nif: Int,
    val phone: String?,
    val parish: String,
    val county: String,
    val associationName: String,
    val associationNum: Int
) {
    fun checkNifSize() = nif.toString().length == 9
}

data class LoginInputModel(
    val user: String,
    val password: String
)

data class EditProfileInputModel(
    val username: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val location: Location,
    val association: Association
)

data class GetUserModel(
    val id: Int,
    val username: String,
    val nif: Int,
    val email: String,
    val phone: String?,
    val firstName: String,
    val lastName: String,
    val role: String,
    val association: Association,
    val location: Location
)

data class UserAndTokenModel(
    val id: Int,
    val username: String,
    val name: String,
    val nif: Int,
    val email: String,
    val phone: String,
    val role: String,
    val location: Location,
    val association: Association,
    val tokenValidation: TokenValidationInfo,
    val createdAt: Long,
    val lastUsedAt: Long
) {
    val userAndToken: Pair<User, Token>
        get() = Pair(
            User(id, username,name, nif, email, phone, role, location, association),
            Token(
                tokenValidation,
                id,
                Instant.fromEpochSeconds(createdAt),
                Instant.fromEpochSeconds(lastUsedAt)
            )
        )
}

data class LoginOutputModel(val userId: Int, val username: String, val token: String, val role: String)

data class TokenModel(val token: String)

data class SessionInputModel(val userId: Int, val token: String)

data class SessionValidation(val valid: Boolean)

data class FileModel(val file: ByteArray, val fileName: String, val contentType: String)

data class PendingCouncils(
    val id: Int,
    val email: String,
    val nif: Int,
    val location: Location,
    val association: Association
)