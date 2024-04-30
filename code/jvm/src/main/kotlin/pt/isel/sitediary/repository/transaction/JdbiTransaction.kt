package pt.isel.sitediary.repository.transaction

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.repository.AddressRepository
import pt.isel.sitediary.repository.TokenRepository
import pt.isel.sitediary.repository.UserRepository
import pt.isel.sitediary.repository.WorkRepository
import pt.isel.sitediary.repository.jdbi.JdbiAddress
import pt.isel.sitediary.repository.jdbi.JdbiToken
import pt.isel.sitediary.repository.jdbi.JdbiUser
import pt.isel.sitediary.repository.jdbi.JdbiWork

class JdbiTransaction(handle: Handle) : Transaction {

    override val usersRepository: UserRepository = JdbiUser(handle)
    override val workRepository: WorkRepository = JdbiWork(handle)
    override val tokenRepository: TokenRepository = JdbiToken(handle)
    override val addressRepository: AddressRepository = JdbiAddress(handle)

}