plugins {
    java
    application
    id("com.adarshr.test-logger") version "3.2.0"
    checkstyle
    jacoco
    id("org.sonarqube") version "4.3.1.3277"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
}

group = "com.example"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val junitVersion = "5.9.2"
val assertjVersion = "3.24.2"
val mockitoVersion = "5.5.0"

repositories {
    mavenCentral()
}

dependencies {
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("com.example.template.App")
}

testing {
    suites {
        withType<JvmTestSuite>().configureEach {
            useJUnitJupiter()

            dependencies {
                implementation(project())
                implementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
                implementation("org.assertj:assertj-core:${assertjVersion}")
                implementation("org.mockito:mockito-core:${mockitoVersion}")
            }
        }

        val test by getting(JvmTestSuite::class) {
            dependencies {}
        }

        register<JvmTestSuite>("integrationTest") {
            testType.set(TestSuiteType.INTEGRATION_TEST)

            dependencies {}

            sources {
                java {
                    setSrcDirs(listOf("src/it/java"))
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(tasks.test)
                    }
                }
            }
        }
    }
}

tasks.check {
    dependsOn(
        tasks.named("integrationTest"),
        tasks.named("jacocoTestReport")
    )
}

tasks.withType<JacocoReport>() {
    dependsOn(tasks.named("integrationTest"))
    executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))

    reports {
        html.required.set(true)
    }
}

checkstyle {
    isShowViolations = true
    toolVersion = "10.2"
}

tasks.sonar {
    dependsOn("check")
}

sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", env.fetch("SONAR_TOKEN"))
        property("sonar.projectKey", "fanatixan_java-template")
        property("sonar.organization", "fanatixan")
        property("sonar.projectName", "java-template")
    }
}

tasks.wrapper {
    gradleVersion = "8.1.1"
}
