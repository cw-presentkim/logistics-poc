import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("io.kotest") version "0.4.10"
    id("nu.studer.jooq") version "8.2.1"
    id("com.google.cloud.tools.jib") version "3.3.2"
    id("org.sonarqube") version "4.2.1.3168"
}

group = "com.coway.america"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    modules {
        // logging 구현체를 기본 설정인 logback 에서 log4j2 로 변경
        module("org.springframework.boot:spring-boot-starter-logging") {
            replacedBy(
                    "org.springframework.boot:spring-boot-starter-log4j2",
                    "Use Log4j2 instead of Logback for performance"
            )
        }
    }

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")
    // support for joog generated code validation annotation
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    // support for kotlin logging facade
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.2.0")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }

    testImplementation("io.kotest:kotest-runner-junit5:5.6.2")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("io.kotest:kotest-assertions-json:5.6.2")
    testImplementation("io.kotest:kotest-framework-datatest:5.6.2")
    testImplementation("io.kotest:kotest-extensions-junitxml-jvm:5.6.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.4")

    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    // Liquibase 를 이용한 jooq code generator 를 위한 의존성 라이브러리
    jooqGenerator("org.jooq:jooq-meta-extensions-liquibase:${dependencyManagement.importedProperties["jooq.version"]}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    reports {
        junitXml.required.set(false)
    }
    systemProperty("gradle.build.dir", project.buildDir)
}

detekt {
    autoCorrect = true
    ignoreFailures = true
}

// [S] JOOQ Code generation configuration
jooq {
    version.set(dependencyManagement.importedProperties["jooq.version"])

    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = Logging.WARN
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.extensions.liquibase.LiquibaseDatabase"
                        isOutputSchemaToDefault = true
                        properties = listOf(
                            Property()
                                .withKey("rootPath")
                                .withValue("${project.rootDir}/src/main/resources"),
                            Property()
                                .withKey("scripts")
                                .withValue("/db/changelog/db.changelog-master.yaml")
                        )
                    }
                    generate.apply {
                        isGeneratedAnnotation = true
                        isValidationAnnotations = true
                        isSpringAnnotations = true
                        isFluentSetters = true
                        isRecords = true
                    }
                    target.apply {
                        packageName = "com.coway.america.persistence"
                        directory = "build/generated/sources/jooq/kotlin/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
// [E] JOOQ Code generation configuration

// [S] JIB Configuration
jib {
    from {
        image = "public.ecr.aws/amazoncorretto/amazoncorretto:17.0.7-al2023-headless"
        platforms {
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    container {
        jvmFlags = listOf(
            "-XX:MaxRAMPercentage=75.0",
            "-XX:FlightRecorderOptions=stackdepth=256",
            "-Djava.net.preferIPv4Stack=true",
            "-Duser.language=en",
            "-Duser.timezone=UTC"
        )
        ports = listOf("8080")
    }
}
// [E] JIB Configuration

// [S] SonarQube
sonarqube {
    properties {
        properties["sonar.projectName"] = "REST API 서버 예제"
        properties["sonar.projectDescription"] = "\"미신사\" 프로젝트 REST API 서버 예제 입니다."
    }
}
// [E] SonarQube
