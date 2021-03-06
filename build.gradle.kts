import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.kuroszdaniel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-data-cassandra")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.kafka:spring-kafka:2.8.7")
	implementation("org.apache.kafka:kafka-streams:3.1.0")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.apache.kafka:kafka-streams-test-utils:3.1.0")
// https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka-test
	testImplementation("org.springframework.kafka:spring-kafka-test:2.8.7")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
