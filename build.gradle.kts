plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    enabled = false
}
