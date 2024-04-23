package pt.isel.sitediary.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.sitediary.repository.AddressRepository
import pt.isel.sitediary.utils.Location

class JdbiAddress (private val handle: Handle): AddressRepository {
    override fun checkParishExists(parish: String): Location? = handle.createQuery(
        "select freguesia, concelho, distrito from localidades where freguesia = :parish"
    )
        .bind("parish", parish)
        .mapTo(Location::class.java)
        .singleOrNull()
}