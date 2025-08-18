plugins {
	application
	checkstyle
	jacoco
	id("io.freefair.lombok") version "8.14"
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("se.patrikerdes.use-latest-versions") version "0.2.19"
	id("com.github.ben-manes.versions") version "0.52.0"
	id("org.sonarqube") version "6.2.0.5505"
	id("io.sentry.jvm.gradle") version "5.9.0"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
	mainClass = "hexlet.code.AppApplication"
}

sonar {
	properties {
		property("sonar.projectKey", "prvmjsky_java-project-99")
		property("sonar.organization", "prvmjsky")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

sentry {
	includeSourceContext = true
	org = "sneeds-feed-seed"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.sentryBundleSourcesJava {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}

tasks.withType<JavaCompile>{
	options.compilerArgs.add("-parameters")
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	val mapstructVer = "1.6.3"
	val springdocVer = "2.6.0"

	implementation("org.mapstruct:mapstruct:$mapstructVer")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVer")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:$springdocVer")
	implementation("net.datafaker:datafaker:2.4.4")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("io.sentry:sentry-opentelemetry-agent:8.19.0")

	annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVer")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springdoc:springdoc-openapi-starter-webmvc-api:$springdocVer")
	testImplementation("org.instancio:instancio-junit:5.5.1")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	jacocoAgent("org.jacoco:org.jacoco.agent:0.8.13")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
	}
}
