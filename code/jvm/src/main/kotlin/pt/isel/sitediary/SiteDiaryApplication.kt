package pt.isel.sitediary

import kotlinx.datetime.Clock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import kotlin.time.Duration.Companion.hours

@SpringBootApplication
class SiteDiaryApplication {


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
