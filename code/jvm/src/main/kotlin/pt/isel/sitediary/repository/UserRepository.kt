package pt.isel.sitediary.repository


import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.utils.Location
import pt.isel.sitediary.utils.User

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
    ) : Int
    fun login(user: String, password: String) : Int?
    fun getUser(id: Int) : GetUserModel?
    fun getUserByUsername(username: String) : GetUserModel?
    fun updatePhoneNumber(id: Int, number : String)
    fun checkUsernameTaken(username: String): Boolean
    fun checkEmailInUse(email: String): Boolean
}