package pt.isel.sitediary.repository.jdbi


import org.jdbi.v3.core.Handle
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.utils.Location
import pt.isel.sitediary.utils.User
import pt.isel.sitediary.repository.UserRepository

class JdbiUser(private val handle: Handle): UserRepository {
    override fun createUser(
        email: String,
        role: String,
        username: String,
        password: String,
        firstName: String,
        lastName: String,
        phone: String?,
        location: Location
    ): Int = handle.createUpdate(
    "insert into utilizador(email, role, username, password, nome, apelido, telefone, freguesia, concelho, distrito)" +
            "values (:email, :role, :username, :password, :nome, :apelido, :telefone, :freguesia, :concelho, :distrito)"
    )
    .bind("email", email)
    .bind("role", role)
    .bind("username", username)
    .bind("password", password)
    .bind("nome", firstName)
    .bind("apelido", lastName)
    .bind("telefone", phone)
    .bind("freguesia", location.parish)
    .bind("concelho", location.county)
    .bind("distrito", location.district)
    .executeAndReturnGeneratedKeys()
    .mapTo(Int::class.java)
    .one()

    override fun login(user: String, password: String): Int? = handle.createQuery(
        "select id from UTILIZADOR where (username = :username or email = :email) and password = :password"
    )
        .bind("username", user)
        .bind("email", user)
        .bind("password", password)
        .mapTo(Int::class.java)
        .singleOrNull()


    override fun getUser(id: Int): GetUserModel? = handle.createQuery(
        "select id, username, email, telefone, role, freguesia, concelho, distrito from UTILIZADOR where id = :id "
    )
        .bind("id", id)
        .mapTo(GetUserModel::class.java)
        .singleOrNull()

    override fun getUserByUsername(username: String): GetUserModel? =
        handle.createQuery("select * from UTILIZADOR where username = :username")
        .bind("username", username)
        .mapTo(GetUserModel::class.java)
        .singleOrNull()

    override fun getFullUser(id: Int): User? = handle.createQuery(
        "select * from UTILIZADOR where id = :id"
    )
        .bind("id", id)
        .mapTo(User::class.java)
        .singleOrNull()

    override fun updatePhoneNumber(id: Int, number: String) {
        handle.createUpdate("update UTILIZADOR set telefone = :phone where id = :id")
            .bind("phone", number)
            .bind("id", id)
            .execute()
    }

    override fun checkUsernameTaken(username: String): Boolean = handle.createQuery(
       "select count(*) from UTILIZADOR where username = :username"
    )
        .bind("username", username)
        .mapTo(Int::class.java)
        .single() == 1

    override fun checkEmailInUse(email: String): Boolean = handle.createQuery(
        "select count(*) from UTILIZADOR where email = :email"
    )
        .bind("email", email)
        .mapTo(Int::class.java)
        .single() == 1
}