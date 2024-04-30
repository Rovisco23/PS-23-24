package pt.isel.sitediary

import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import pt.isel.sitediary.domainmodel.authentication.Sha256TokenEncoder
import pt.isel.sitediary.domainmodel.authentication.UsersDomainConfig
import pt.isel.sitediary.utils.configureWithAppRequirements
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
class SiteDiaryApplication {

	@Bean
	fun jdbi(): Jdbi {
		val jdbcDatabaseURL =
			System.getenv("JDBC_DATABASE_URL")
				?: "jdbc:postgresql://localhost/postgres?user=postgres&password=ps2324"
		val dataSource = PGSimpleDataSource()
		dataSource.setURL(jdbcDatabaseURL)
		return Jdbi.create(dataSource).configureWithAppRequirements()
	}

	@Bean
	fun tokenEncoder() = Sha256TokenEncoder()

	@Bean
	fun clock() = Clock.System

	@Bean
	fun usersDomainConfig() = UsersDomainConfig(
		tokenSizeInBytes = 256 / 8,
		tokenTtl = 24.hours,
		tokenRollingTtl = 1.hours,
		maxTokensPerUser = 1
	)
}

fun main(args: Array<String>) {
	runApplication<SiteDiaryApplication>(*args)
}
