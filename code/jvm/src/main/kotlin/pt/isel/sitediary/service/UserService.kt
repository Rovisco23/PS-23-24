package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenExternalInfo
import pt.isel.sitediary.domainmodel.authentication.UsersDomain
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.model.EditProfileInputModel
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.PendingInputModel
import pt.isel.sitediary.model.SessionValidation
import pt.isel.sitediary.model.SignUpInputModel
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.Errors
import pt.isel.sitediary.utils.Result
import pt.isel.sitediary.utils.failure
import pt.isel.sitediary.utils.success

typealias UserCreationResult = Result<Errors, Unit>
typealias LoginResult = Result<Errors, TokenExternalInfo>
typealias LogoutResult = Result<Errors, Unit>
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
        if (rep.checkUsernameTaken(user.username) != null) { // username == null
            failure(Errors.usernameAlreadyInUse)
        } else if (rep.checkEmailInUse(user.email)) {
            failure(Errors.emailAlreadyInUse)
        } else if (user.role != "OPERÁRIO" && user.role != "CÂMARA") {
            failure(Errors.invalidRole)
        } else if (!checkPhoneNumberFormat(user.phone)) {
            failure(Errors.invalidPhoneNumber)
        } else if (!user.checkNifSize()) {
            failure(Errors.invalidNif)
        } else {
            val location = it.addressRepository.getLocation(user.parish, user.county)
            if (location == null) {
                failure(Errors.invalidLocation)
            } else if (rep.checkDummyEmail(user.email)) { // email != null
                rep.updateDummyUser(user, location, user.role != "OPERÁRIO")
                success(Unit)
            } else {
                rep.createUser(user, location, user.role != "OPERÁRIO")
                success(Unit)
            }
        }
    }

    /*
    * else if (rep.checkEmailInUse(user.email)) { // email != null
            val l = it.addressRepository.getLocation(user.parish, user.county)
            if (l == null) {
                failure(Errors.invalidLocation)
            } else {
                val location = Location(l.district, l.county, l.parish)
                val id = rep.createUser(user, location)
                if (user.role != "OPERÁRIO") {
                    rep.insertPending(id, user.role)
                }
                success(Unit)
            }
        }*/


    fun login(username: String, password: String): LoginResult {
        if (username.isBlank() || password.isBlank()) {
            failure(Errors.invalidLoginParamCombination)
        }
        return transactionManager.run {
            val uRep = it.usersRepository
            val tRep = it.tokenRepository
            val userId = uRep.login(username, password)
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
                val user = uRep.getUserById(userId)
                if (user == null) {
                    failure(Errors.userNotFound)
                } else {
                    success(
                        TokenExternalInfo(
                            tokenValue = tokenValue,
                            userId = userId,
                            username = user.username,
                            role = user.role,
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
                success(Unit)
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

    fun editProfile(user: User, editUser: EditProfileInputModel): UserEditResult = transactionManager.run {
        val rep = it.usersRepository
        if (user.username != editUser.username && rep.checkUsernameTaken(editUser.username) != null) {
            failure(Errors.usernameAlreadyInUse)
        } else if (!checkPhoneNumberFormat(editUser.phone)) {
            failure(Errors.invalidPhoneNumber)
        } else {
            val location = it.addressRepository.getLocation(editUser.location.parish, editUser.location.county)
            if (location == null) {
                failure(Errors.invalidLocation)
            } else {
                val updatedUser = GetUserModel(
                    user.id,
                    editUser.username,
                    user.nif,
                    user.email,
                    editUser.phone,
                    editUser.firstName,
                    editUser.lastName,
                    user.role,
                    editUser.association,
                    location
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

    fun changeProfilePicture(file: FileModel?, userId: Int) = transactionManager.run {
        val rep = it.usersRepository
        val user = rep.getUserById(userId)
        if (user == null) {
            failure(Errors.userNotFound)
        } else {
            if (rep.checkProfilePictureExists(userId) == null) {
                rep.insertProfilePicture(userId, file!!)
            } else {
                if (file != null) rep.changeProfilePicture(userId, file) else rep.removeProfilePicture(userId)
            }
            success(Unit)
        }
    }

    fun getProfilePicture(userId: Int): GetProfilePictureResult =
        transactionManager.run {
            if (userId <= 0) success(null)
            else {
                val rep = it.usersRepository
                val user = rep.getUserById(userId)
                if (user == null) {
                    failure(Errors.userNotFound)
                } else {
                    val pfp = rep.getProfilePicture(userId)
                    success(pfp)
                }
            }

        }


    fun answerPendingCouncil(pendingInput: PendingInputModel, authUser: User) = transactionManager.run {
        val userRep = it.usersRepository
        if (authUser.role != "ADMIN") {
            failure(Errors.forbidden)
        } else {
            val user = userRep.getUserById(pendingInput.userId)
            if (user == null) {
                failure(Errors.userNotFound)
            } else {
                if (pendingInput.accepted) {
                    userRep.acceptCouncil(pendingInput.userId)
                    success(Unit)
                } else {
                    userRep.declineCouncil(pendingInput.userId)
                    success(Unit)
                }
            }
        }
    }

    fun getAllPendingCouncils(user: User) = transactionManager.run { //: List<PendingCouncils>
        val rep = it.usersRepository
        if (user.role != "ADMIN") {
            failure(Errors.forbidden)
        } else {
            val list = rep.getAllPendingCouncils()
            success(list)
        }
    }

    fun getAllUsers(user: User) = transactionManager.run {
        if (user.role != "ADMIN") {
            failure(Errors.forbidden)
        } else {
            val users = it.usersRepository.getAllUsers()
            success(users)
        }
    }

    private fun checkPhoneNumberFormat(phone: String?): Boolean {
        if (phone.isNullOrBlank()) {
            return true
        }
        if (phone.length > 9 || phone.toIntOrNull() == null) return false
        return true
    }
}
