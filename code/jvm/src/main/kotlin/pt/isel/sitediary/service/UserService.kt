package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenExternalInfo
import pt.isel.sitediary.domainmodel.authentication.UsersDomain
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.model.EditProfileInputModel
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.SessionValidation
import pt.isel.sitediary.model.SignUpInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.*

typealias UserCreationResult = Result<Errors, User>
typealias LoginResult = Result<Errors, TokenExternalInfo>
typealias LogoutResult = Result<Errors, String>
typealias UserEditResult = Result<Errors, GetUserModel>
typealias SessionResult = Result<Errors, SessionValidation>
typealias GetProfilePictureResult = Result<Errors, FileModel?>

@Component
class UserService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {
    fun createUser(user: SignUpInputModel): UserCreationResult = transactionManager.run {
        val rep = it.usersRepository
        if (rep.checkUsernameTaken(user.username) != null) {
            failure(Errors.usernameAlreadyInUse)
        } else if (rep.checkEmailInUse(user.email)) {
            failure(Errors.emailAlreadyInUse)
        } else if (user.role != "OPERÁRIO" && user.role != "CÂMARA") {
            failure(Errors.invalidRole)
        } else if (!checkPhoneNumberFormat(user.phone)) {
            failure(Errors.invalidPhoneNumber)
        } else {
            val l = it.addressRepository.getLocation(user.parish, user.county)
            if (l == null) {
                failure(Errors.invalidLocation)
            } else {
                val location = Location(l.district, l.county, l.parish)
                val id = rep.createUser(user, location)
                success(User(id, user.username, user.nif, user.email, user.phone, user.role, location))
            }
        }
    }

    fun login(user: String, password: String): LoginResult {
        if (user.isBlank() || password.isBlank()) {
            failure(Errors.invalidLoginParamCombination)
        }
        return transactionManager.run {
            val uRep = it.usersRepository
            val tRep = it.tokenRepository
            val userId = uRep.login(user, password)
            if (userId == null) {
                failure(Errors.invalidLoginParamCombination)
            } else {
                val tokenValue = usersDomain.generateTokenValue()
                val now = clock.now()
                val newToken = Token(
                    tokenValidationInfo = usersDomain.createTokenValidationInformation(tokenValue),
                    userId = userId,
                    createdAt = now,
                    lastUsedAt = now
                )
                tRep.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
                val username = uRep.getUserById(userId)
                if (username == null) {
                    failure(Errors.userNotFound)
                } else {
                    success(
                        TokenExternalInfo(
                            tokenValue = tokenValue,
                            userId = userId,
                            username = username.username,
                            tokenExpiration = usersDomain.getTokenExpiration(newToken)
                        )
                    )
                }

            }
        }
    }

    fun checkSession(userId: Int, token: String): SessionResult = transactionManager.run {
        val rep = it.tokenRepository
        if (!rep.checkSession(userId, token)) {
            success(SessionValidation(false))
        } else {
            success(SessionValidation(true))
        }
    }

    fun logout(token: String): LogoutResult {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return transactionManager.run {
            val out = it.tokenRepository.deleteToken(tokenValidationInfo)
            if (!out) {
                failure(Errors.noUserLoggedIn)
            } else {
                success("Logout successful")
            }
        }
    }

    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return transactionManager.run {
            val usersRepository = it.usersRepository
            val tokensRepository = it.tokenRepository
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken = usersRepository.getUserByToken(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                tokensRepository.updateLastUsedToken(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }

    fun editProfile(userId: Int, editUser: EditProfileInputModel): UserEditResult = transactionManager.run {
        val rep = it.usersRepository
        val u = rep.getUserById(userId)
        if (u == null) {
            failure(Errors.userNotFound)
        } else if (rep.checkUsernameTaken(editUser.username) != userId) {
            failure(Errors.usernameAlreadyInUse)
        } else if (!checkPhoneNumberFormat(editUser.phone)) {
            failure(Errors.invalidPhoneNumber)
        } else {
            val location = it.addressRepository.getLocation(editUser.parish, editUser.county)
            if (location == null) {
                failure(Errors.invalidLocation)
            } else {
                val updatedUser = u.copy(
                    username = editUser.username,
                    firstName = editUser.firstName,
                    lastName = editUser.lastName,
                    phone = editUser.phone,
                    location = location
                )
                rep.editProfile(updatedUser)
                success(updatedUser)
            }
        }
    }

    fun getUserById(id: Int) = transactionManager.run {
        val user = it.usersRepository.getUserById(id)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            success(user)
        }
    }

    fun getUserByUsername(username: String) = transactionManager.run {
        val user = it.usersRepository.getUserByUsername(username)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            success(user)
        }
    }

    fun changeProfilePicture(file: FileModel, userId: Int) = transactionManager.run {
        val rep = it.usersRepository
        val user = rep.getUserById(userId)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            rep.changeProfilePicture(userId, file)
            success(user)
        }
    }

    fun getProfilePicture(id: Int): GetProfilePictureResult = transactionManager.run {
        val rep = it.usersRepository
        val user = rep.getUserById(id)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            val pfp = rep.getProfilePicture(id)
            success(pfp)
        }
    }
}

fun checkPhoneNumberFormat(phone: String?): Boolean {
    if (phone.isNullOrBlank()) {
        return true
    }
    if (phone.length > 9 || phone.toIntOrNull() == null) return false
    return true
}