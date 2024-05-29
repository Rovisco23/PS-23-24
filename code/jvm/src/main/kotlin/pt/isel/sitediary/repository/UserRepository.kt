package pt.isel.sitediary.repository

import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenValidationInfo
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.SignUpInputModel

interface UserRepository {
    fun createUser(user:SignUpInputModel, location: Location): Int
    fun login(user: String, password: String): Int?
    fun getUserById(id: Int): GetUserModel?
    fun getUserByEmail(email: String): GetUserModel?
    fun getUserByUsername(username: String): GetUserModel?
    fun updatePhoneNumber(id: Int, number: String)
    fun checkEmailInUse(email: String): Boolean
    fun checkUsernameTaken(username: String): Int?
    fun editProfile(user: GetUserModel)
    fun getUserByToken(token: TokenValidationInfo): Pair<User, Token>?
    fun changeProfilePicture(id: Int, picture: FileModel)
    fun removeProfilePicture(id: Int)
    fun getProfilePicture(id: Int): FileModel?
    fun insertPending(id: Int, role: String)
    fun acceptCouncil(userId: Int)
}