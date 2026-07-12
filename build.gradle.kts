plugins {
	java
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.josephmfaulkner"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("software.amazon.awssdk:dynamodb-enhanced:2.47.2")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("software.amazon.dynamodb:DynamoDBLocal:3.3.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform {
        includeTags("unit")
        excludeTags("integration")
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register("unitTests") {
    description = "Alias for the standard 'test' task to run unit tests."
    group = "verification"
    dependsOn(tasks.test) 
}

tasks.register<Test>("integrationTests") {
    description = "Runs the integration test suite."
    group = "verification"

    useJUnitPlatform {
        includeTags("integration")
    }

    testLogging {
        events("passed", "skipped", "failed")
    }

    outputs.upToDateWhen { false }
}