plugins {
    java
    application
    id("com.adarshr.test-logger") version "3.2.0"
    checkstyle
    jacoco
    id("org.sonarqube") version "4.3.1.3277"
    id("io.snyk.gradle.plugin.snykplugin") version "0.5"
    id("co.uzzu.dotenv.gradle") version "2.0.0"
    id("com.dorongold.task-tree") version "2.1.1"
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

        getByName<JvmTestSuite>("test") {
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

tasks.withType<Test> {
    finalizedBy("jacocoTestReport")
}

tasks.check {
    dependsOn(
        tasks.named("integrationTest"),
        tasks.named("jacocoTestReport")
    )
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.named("integrationTest"))
    executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

checkstyle {
    isShowViolations = true
    toolVersion = "10.12.1"
}

tasks.sonar {
    dependsOn("check")
}

sonar {
    properties {
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", env.fetch("SONAR_TOKEN"))
        property("sonar.projectKey", "fanatixan_java-template")
        property("sonar.organization", "fanatixan")
        property("sonar.projectName", "java-template")
        property("sonar.sources", "src/main")
        property("sonar.tests", "src/test,src/it")
        property("sonar.junit.reportPaths", "${buildDir}/test-results/test,${buildDir}/test-results/integrationTest")
        property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
    }
}

snyk {
    setSeverity("low")
    setApi(env.fetch("SNYK_TOKEN", ""))
}

tasks.wrapper {
    gradleVersion = "8.1.1"
}
