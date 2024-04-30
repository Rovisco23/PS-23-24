package pt.isel.sitediary.repository.transaction

import pt.isel.sitediary.repository.AddressRepository
import pt.isel.sitediary.repository.TokenRepository
import pt.isel.sitediary.repository.UserRepository
import pt.isel.sitediary.repository.WorkRepository

interface Transaction {
    val usersRepository: UserRepository
    val workRepository: WorkRepository
    val tokenRepository: TokenRepository
    val addressRepository: AddressRepository
}