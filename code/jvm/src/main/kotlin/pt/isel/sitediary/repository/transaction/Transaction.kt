package pt.isel.sitediary.repository.transaction

import pt.isel.sitediary.repository.AddressRepository
import pt.isel.sitediary.repository.UserRepository

interface Transaction {
    val usersRepository: UserRepository
    val addressRepository: AddressRepository
}