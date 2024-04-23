package pt.isel.sitediary.service

import org.springframework.stereotype.Component
import pt.isel.sitediary.repository.transaction.TransactionManager
import pt.isel.sitediary.utils.*

typealias UserCreationResult = Result<Errors, User>

@Component
class UserService(
    private val transactionManager: TransactionManager
) {
    fun createUser(
        email: String,
        role: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        parish: String
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
            //verificação da Location futuramente
            val location = Location("Fixolândia", "Fixoconcelho", parish)
            val id = rep.createUser(email, role, username, password, firstName, lastName, phone, location)
            success(User(id, username, email, phone, role, location))
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