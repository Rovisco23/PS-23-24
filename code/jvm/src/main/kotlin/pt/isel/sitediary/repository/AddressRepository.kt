package pt.isel.sitediary.repository

import pt.isel.sitediary.model.LocationModel

interface AddressRepository {
    fun getLocation(parish: String, county: String): LocationModel?
    fun getParishes(county: String, district: String): List<String>
    fun getCountys(district: String): List<String>
    fun getDistricts(): List<String>
}