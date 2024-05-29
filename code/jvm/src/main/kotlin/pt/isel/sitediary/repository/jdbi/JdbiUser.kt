package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.domainmodel.authentication.Token
import pt.isel.sitediary.domainmodel.authentication.TokenValidationInfo
import pt.isel.sitediary.domainmodel.user.User
import pt.isel.sitediary.domainmodel.work.Location
import pt.isel.sitediary.model.FileModel
import pt.isel.sitediary.model.GetUserModel
import pt.isel.sitediary.model.SignUpInputModel
import pt.isel.sitediary.model.UserAndTokenModel
import pt.isel.sitediary.repository.UserRepository

class JdbiUser(private val handle: Handle) : UserRepository {
    override fun createUser(user: SignUpInputModel, location: Location): Int = handle.createUpdate(
        "insert into utilizador(email, role, username, password, nome, apelido, nif, telefone, freguesia, " +
                "concelho, distrito, associacao_nome, associacao_numero) values (:email, :role, :username, " +
                ":password, :nome, :apelido, :nif, :telefone, :freguesia, :concelho, :distrito, :associacao_nome," +
                " :associacao_numero)"
    )
        .bind("email", user.email)
        .bind("role", "OPERÁRIO")
        .bind("username", user.username)
        .bind("password", user.password)
        .bind("nome", user.firstName)
        .bind("apelido", user.lastName)
        .bind("nif", user.nif)
        .bind("telefone", user.phone)
        .bind("freguesia", location.parish)
        .bind("concelho", location.county)
        .bind("distrito", location.district)
        .bind("associacao_nome", user.associationName)
        .bind("associacao_numero", user.associationNum)
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


    override fun getUserById(id: Int): GetUserModel? = handle.createQuery(
        "select * from UTILIZADOR where id = :id "
    )
        .bind("id", id)
        .mapTo(GetUserModel::class.java)
        .singleOrNull()

    override fun getUserByEmail(email: String): GetUserModel? = handle.createQuery(
        "select * from UTILIZADOR where email = :email "
    )
        .bind("email", email)
        .mapTo(GetUserModel::class.java)
        .singleOrNull()

    override fun getUserByUsername(username: String): GetUserModel? =
        handle.createQuery("select * from UTILIZADOR where username = :username")
            .bind("username", username)
            .mapTo(GetUserModel::class.java)
            .singleOrNull()

    override fun updatePhoneNumber(id: Int, number: String) {
        handle.createUpdate("update UTILIZADOR set telefone = :phone where id = :id")
            .bind("phone", number)
            .bind("id", id)
            .execute()
    }

    override fun checkEmailInUse(email: String): Boolean = handle.createQuery(
        "select count(*) from UTILIZADOR where email = :email"
    )
        .bind("email", email)
        .mapTo(Int::class.java)
        .single() == 1

    override fun checkUsernameTaken(username: String): Int? = handle.createQuery(
        "select id from UTILIZADOR where username = :username"
    )
        .bind("username", username)
        .mapTo(Int::class.java)
        .singleOrNull()

    override fun editProfile(user: GetUserModel) {
        handle.createUpdate(
            "update utilizador set username = :username, telefone = :phone, nome = :firstName, apelido = :lastName, " +
                    "freguesia = :parish, concelho = :county, distrito = :district where id = :id"
        )
            .bind("username", user.username)
            .bind("phone", user.phone)
            .bind("firstName", user.firstName)
            .bind("lastName", user.lastName)
            .bind("parish", user.location.parish)
            .bind("county", user.location.county)
            .bind("district", user.location.district)
            .bind("id", user.id)
            .execute()
    }

    override fun getUserByToken(token: TokenValidationInfo): Pair<User, Token>? = handle.createQuery(
        "select id, username, nif, email, telefone, role, freguesia, concelho, distrito, token_validation, created_at," +
                " last_used_at from utilizador inner join sessao  on utilizador.id = sessao.uId " +
                "where token_validation = :validation_information"
    )
        .bind("validation_information", token.validationInfo)
        .mapTo(UserAndTokenModel::class.java)
        .singleOrNull()?.userAndToken

    override fun insertProfilePicture(id: Int, picture: FileModel) {
        handle.createUpdate("insert into profile_picture(user_id, name, type, img) values (:id, :name, :type, :img)")
            .bind("id", id)
            .bind("name", picture.filename)
            .bind("type", picture.contentType)
            .bind("img", picture.file)
            .execute()
    }

    override fun changeProfilePicture(id: Int, picture: FileModel) {
        handle.createUpdate("update profile_picture set name = :name, type = :type, img = :img where user_id = :id")
            .bind("id", id)
            .bind("name", picture.filename)
            .bind("type", picture.contentType)
            .bind("img", picture.file)
            .execute()
    }

    override fun checkProfilePictureExists(id: Int) = handle.createQuery(
        "select user_id from profile_picture where user_id = :id"
    )
        .bind("id", id)
        .mapTo(Int::class.java)
        .singleOrNull()

    override fun removeProfilePicture(id: Int) {
        handle.createUpdate("delete from profile_picture where user_id = :id")
            .bind("id", id)
            .execute()
    }

    override fun getProfilePicture(id: Int): FileModel? = handle.createQuery(
        "select img, name, type from profile_picture where user_id = :id"
    )
        .bind("id", id)
        .mapTo(FileModel::class.java)
        .singleOrNull()

    override fun insertPending(id: Int, role: String) {
        handle.createUpdate("insert into pendente(uId) values (:id)")
            .bind("id", id)
            .execute()
    }

    override fun acceptCouncil(userId: Int) {
        handle.createUpdate("update utilizador set role = 'CÂMARA' where id = :id")
            .bind("id", userId)
            .execute()
        handle.createUpdate("delete from pendente where uId = :id")
            .bind("id", userId)
            .execute()
        addCouncilToExistentWorks(userId)
    }

    private fun addCouncilToExistentWorks(councilId: Int) {
        val councilLocation = handle.createQuery("select distrito, concelho, freguesia from UTILIZADOR where id = :id")
            .bind("id", councilId)
            .mapTo(Location::class.java)
            .single()
        val workIds = handle.createQuery("select id from OBRA where freguesia = :parish and concelho = :county" +
                " and distrito = :district")
            .bind("parish", councilLocation.parish)
            .bind("county", councilLocation.county)
            .bind("district", councilLocation.district)
            .mapTo(String::class.java)
            .list()
        val query = StringBuilder("insert into MEMBRO(uId, oId, role) values ")
        workIds.forEach {
            query.append("('${councilId}', '${it}', 'ESPECTADOR'), ")
        }
        handle.createUpdate(query.toString().dropLast(2)).execute()
    }

}