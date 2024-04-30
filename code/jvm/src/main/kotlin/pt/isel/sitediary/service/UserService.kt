package pt.isel.sitediary.service

import kotlinx.datetime.Clock
import org.springframework.stereotype.Component
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.*

typealias UserCreationResult = Result<Errors, User>
typealias LoginResult = Result<Errors, TokenExternalInfo>
typealias LogoutResult = Result<Errors, String>

@Component
class UserService(
    private val transactionManager: TransactionManager,
    private val usersDomain: UsersDomain,
    private val clock: Clock
) {
    fun createUser(
        email: String,
        role: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        parish: String,
        county: String
    ): UserCreationResult = transactionManager.run {
        val rep = it.usersRepository

        if (rep.checkUsernameTaken(username)) {
            failure(Errors.usernameAlreadyInUse)
        } else if (rep.checkEmailInUse(email)) {
            failure(Errors.emailAlreadyInUse)
        } else if (role != "OPERÁRIO" && role != "CÂMARA") {
            failure(Errors.invalidRole)
        } else if (!checkPhoneNumberFormat(phone)) {
            failure(Errors.invalidPhoneNumber)
        } else {
            val l = it.addressRepository.getLocation(parish, county)
            if (l == null) {
                failure(Errors.invalidLocation)
            } else {
                val location = Location(l.district, l.county, l.parish)
                val id = rep.createUser(email, role, username, password, firstName, lastName, phone, location)
                success(User(id, username, email, phone, role, location))
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
                val username = uRep.getUser(userId)
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

    fun logout(token: String): LogoutResult {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return transactionManager.run {
            val out = it.tokenRepository.deleteToken(tokenValidationInfo)
            if (!out) {
                failure(Errors.internalError)
            } else {
                success("Logout successful")
            }
        }
    }

    fun editProfile(
        userId: Int,
        username: String?,
        firstName: String?,
        lastName: String?,
        phone: String?,
        parish: String?,
        county: String?
    ) {
        transactionManager.run {
            val user = it.usersRepository.getUser(userId)
            if (user == null) {
                failure(Errors.userNotFound)
            } else {
                val location = it.addressRepository.getLocation(parish ?: user.location.parish, county ?: user.location.county)
                if (location == null) {
                    failure(Errors.invalidLocation)
                } else {
                    val updatedUser = user.copy(
                        username = username ?: user.username,
                        firstName = firstName ?: user.firstName,
                        lastName = lastName ?: user.lastName,
                        phone = phone ?: user.phone,
                        location = Location(location.district, location.county, location.parish)
                    )
                    it.usersRepository.updateUser(updatedUser)
                    success(updatedUser)
                }
            }
        }

    }

    fun getUserById(id: Int) = transactionManager.run {
        val user = it.usersRepository.getUser(id)
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
}

fun checkPhoneNumberFormat(phone: String?): Boolean {
    if (phone.isNullOrBlank()) {
        return true
    }
    if (phone.length > 9 || phone.toIntOrNull() == null) return false
    return true
}