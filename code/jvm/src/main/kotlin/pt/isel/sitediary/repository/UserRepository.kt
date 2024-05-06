package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenValidationInfo
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.model.GetUserModel

interface UserRepository {
    fun createUser(
        email: String,
        role: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        location: Location
    ): Int

    fun login(user: String, password: String): Int?
    fun getUserById(id: Int): GetUserModel?
    fun getUserByUsername(username: String): GetUserModel?
    fun updatePhoneNumber(id: Int, number: String)
    fun checkEmailInUse(email: String): Boolean
    fun checkUsernameTaken(username: String): Int?
    fun editProfile(user: GetUserModel)
    fun getUserByToken(token: TokenValidationInfo): Pair<User, Token>?
}