plugins {
    java
    application
    alias(libs.plugins.test.logger)
    checkstyle
    jacoco
    alias(libs.plugins.sonar)
    alias(libs.plugins.snyk)
    alias(libs.plugins.dotenv)
    alias(libs.plugins.versions)
    alias(libs.plugins.version.catalog.update)
}

group = "com.example"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

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
                implementation(libs.junit.jupiter)
                implementation(libs.assertj.core)
                implementation(libs.mockito.core)
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
    toolVersion = libs.versions.checkstyle.get()
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
    gradleVersion = "8.4"
}
