package pt.isel.sitediary

import kotlinx.datetime.Clock
import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import pt.isel.sitediary.domainmodel.authentication.Sha256TokenEncoder
import pt.isel.sitediary.domainmodel.authentication.UsersDomainConfig
import pt.isel.sitediary.utils.configureWithAppRequirements
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
class SiteDiaryApplication {

	private val databaseURL = ""
		//"postgres://admin:CqV3Y4ZMLsyU9MFmuJploD9YKwqmeevH@dpg-cpe89p7109ks73ercrr0-a.frankfurt-postgres.render.com/sitediary"

	@Bean
	fun jdbi(): Jdbi {
		val jdbcDatabaseURL = System.getenv("JDBC_DATABASE_URL") ?: databaseURL
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

@Configuration
class CorsConfiguration : WebMvcConfigurer {
	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:4200") // Adjust this to your Angular app's domain
			.allowedMethods("GET", "POST", "PUT", "DELETE")
			.allowedHeaders("*")
			.exposedHeaders("Location")
	}
}

fun main(args: Array<String>) {
	runApplication<SiteDiaryApplication>(*args)
}
