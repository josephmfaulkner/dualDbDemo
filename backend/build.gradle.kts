plugins {
	java
    `java-test-fixtures`
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
	//implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("software.amazon.awssdk:dynamodb-enhanced:2.47.2")
	
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    
    compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
    //testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("software.amazon.dynamodb:DynamoDBLocal:3.3.0")


    testFixturesImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testFixturesImplementation("software.amazon.awssdk:dynamodb-enhanced:2.47.2")
    testFixturesImplementation("software.amazon.dynamodb:DynamoDBLocal:3.3.0")
}

sourceSets {
    named("testFixtures") {
        java {
            // Overrides the default "src/testFixtures/java"
            setSrcDirs(listOf("src/tests/_testFixtures"))
        }
    }
}


tasks.withType<Test>().configureEach {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

testing {
    suites {
        // 1. Configure the default unit test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter (JUnit 5) instead of the default JUnit 4
            useJUnitJupiter()

            description = "Runs the integration test suite."
            group = "verification"

            sources {
                java {
                    setSrcDirs(listOf("src/tests/unitTests"))
                }
            }

        }

        // 2. Declare a brand new Integration Test suite
        val integrationTests by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            
            dependencies {
                implementation(project()) 
                
                // Add this line to inherit the base class:
                implementation(testFixtures(project())) 
            }

            sources {
                java {
                    // Overrides the default "src/integrationTest/java"
                    setSrcDirs(listOf("src/tests/integrationTests"))
                }
            }
        }
    }
}

// 3. (Optional) Wire the integration tests into the standard build lifecycle
tasks.named("check") {
    dependsOn(testing.suites.named("integrationTests"))
}

configurations {
    // Make integration tests inherit all dependencies from the standard unit tests
    named("integrationTestsImplementation") {
        extendsFrom(configurations.testImplementation.get())
    }
    
    // (Optional) Inherit runtime dependencies too, like database drivers
    named("integrationTestsRuntimeOnly") {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}