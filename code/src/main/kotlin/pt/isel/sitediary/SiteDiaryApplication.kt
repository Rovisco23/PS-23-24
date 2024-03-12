package pt.isel.sitediary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SiteDiaryApplication

fun main(args: Array<String>) {
	runApplication<SiteDiaryApplication>(*args)
}
