package pt.isel.sitediary.repository

import pt.isel.sitediary.utils.Location

interface AddressRepository {
    fun checkParishExists(parish: String): Location?
}