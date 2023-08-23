val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.serialization") version "1.8.22"
	id("io.ktor.plugin") version "2.3.1"
}

group = "com.sslmo"
version = "0.0.1"
application {
	mainClass.set("io.ktor.server.netty.EngineMain")

	val isDevelopment: Boolean = project.ext.has("development")
	applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
	implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")

//    Header
	implementation("io.ktor:ktor-server-forwarded-header:$ktor_version")

//    Request
	implementation("io.ktor:ktor-server-request-validation:$ktor_version")

//    Response
	implementation("io.ktor:ktor-server-status-pages:$ktor_version")

//    Json
	implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
	implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

//    Log
	implementation("ch.qos.logback:logback-classic:$logback_version")

//    Swagger
	implementation("io.github.smiley4:ktor-swagger-ui:2.2.0")
	implementation("io.swagger.core.v3:swagger-annotations:2.2.14")

//    DB
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.ktorm:ktorm-core:3.6.0")
	implementation("com.zaxxer:HikariCP:5.0.1")

//    Auth
	implementation("io.ktor:ktor-server-auth:$ktor_version")
	implementation("at.favre.lib:bcrypt:0.10.2")
//    implementation("com.auth0:java-jwt:4.4.0")
	implementation("io.ktor:ktor-server-auth-jwt:2.3.2")

//    di
	implementation("io.insert-koin:koin-ktor:3.4.3")
	implementation("io.insert-koin:koin-ksp-compiler:1.2.2")



	testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}