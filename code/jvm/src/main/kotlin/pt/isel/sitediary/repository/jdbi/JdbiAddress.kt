package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.sitediary.model.LocationModel
import pt.isel.sitediary.repository.AddressRepository

class JdbiAddress (private val handle: Handle): AddressRepository {
    override fun getLocation(parish: String, county: String): LocationModel? = handle.createQuery(
        "select freguesia, concelho, distrito from localidade where freguesia = :parish and concelho = :county"
    )
        .bind("parish", parish)
        .bind("county", county)
        .mapTo(LocationModel::class.java)
        .singleOrNull()

    override fun getParishes(county: String, district: String): List<String> = handle.createQuery(
        "select freguesia from localidade where concelho = :county and distrito = :district"
    )
        .bind("county", county)
        .bind("district", district)
        .mapTo(String::class.java)
        .list()

    override fun getCountys(district: String): List<String> = handle.createQuery(
    "select concelho from localidade where distrito = :district"
    )
        .bind("district", district)
        .mapTo(String::class.java)
        .list()

    override fun getDistricts(): List<String> = handle.createQuery("" +
            "select distrito from localidade"
    )
        .mapTo(String::class.java)
        .list()

}