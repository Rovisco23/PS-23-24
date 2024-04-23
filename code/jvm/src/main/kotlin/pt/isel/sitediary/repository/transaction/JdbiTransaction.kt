package pt.isel.sitediary.repository.transaction

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.repository.AddressRepository
import pt.isel.sitediary.repository.UserRepository
import pt.isel.sitediary.repository.jdbi.JdbiAddress
import pt.isel.sitediary.repository.jdbi.JdbiUser

class JdbiTransaction(
    private val handle: Handle

) : Transaction {

    override val usersRepository: UserRepository = JdbiUser(handle)
    override val addressRepository: AddressRepository = JdbiAddress(handle)

}