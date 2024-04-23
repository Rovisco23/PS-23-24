import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "pt.isel"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jdbi:jdbi3-core:3.32.0")
	implementation("org.jdbi:jdbi3-kotlin:3.28.0")
	implementation("org.jdbi:jdbi3-postgres:3.32.0")
	implementation("org.postgresql:postgresql:42.5.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
	// If using JUnit Jupiter
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = freeCompilerArgs + "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
