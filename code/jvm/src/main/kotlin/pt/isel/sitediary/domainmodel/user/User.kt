package pt.isel.sitediary.domainmodel.user

import pt.isel.sitediary.domainmodel.work.Location

data class User(
    val id: Int,
    // profile_picture
    val username: String,
    val email: String,
    val phone: String?,
    val role: String,
    val location: Location
)