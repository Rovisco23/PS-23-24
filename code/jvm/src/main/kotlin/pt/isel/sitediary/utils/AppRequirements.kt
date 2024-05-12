package pt.isel.sitediary.utils

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.sitediary.repository.mappers.*

fun Jdbi.configureWithAppRequirements(): Jdbi {
    installPlugin(KotlinPlugin())
    installPlugin(PostgresPlugin())

    registerRowMapper(GetUserMapper())
    registerRowMapper(LocationMapper())
    registerRowMapper(UserAndTokenMapper())
    registerRowMapper(WorkMapper())
    registerRowMapper(WorkSimplifiedMapper())
    registerRowMapper(SessionMapper())

    return this
}