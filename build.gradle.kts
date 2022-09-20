import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"

    id("com.github.davidmc24.gradle.plugin.avro") version "1.4.0"
}

group = "com.kurosz.daniel"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven")
    }
}

avro {
    isCreateSetters.set(true)
    isCreateOptionalGetters.set(false)
    isGettersReturnOptional.set(false)
    isOptionalGettersForNullableFieldsOnly.set(false)
    fieldVisibility.set("PUBLIC")
    outputCharacterEncoding.set("UTF-8")
    stringType.set("String")
    templateDirectory.set(null as String?)
    isEnableDecimalLogicalType.set(true)
}

dependencies {

    implementation("org.springframework.boot:spring-boot-starter-web")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.0")

    implementation("org.springframework.kafka:spring-kafka:2.8.7")
    implementation("org.apache.kafka:kafka-streams:3.1.0")

    implementation("io.confluent:kafka-schema-registry-client:5.3.0")
    implementation("org.apache.avro:avro:1.11.0")
    implementation("io.confluent:kafka-avro-serializer:5.3.0")
    implementation("io.confluent:kafka-streams-avro-serde:7.2.1")
    implementation("com.sksamuel.avro4k:avro4k-core:0.41.0")

    implementation("org.apache.commons:commons-lang3:3.12.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.apache.kafka:kafka-streams-test-utils:3.1.0")
    testImplementation("org.springframework.kafka:spring-kafka-test:2.8.7")

    testImplementation("io.mockk:mockk:1.12.7")
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
