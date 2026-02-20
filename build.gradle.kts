import dev.monosoul.jooq.RecommendedVersions
import org.gradle.kotlin.dsl.annotationProcessor
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.Strategy

val jooqVersion = RecommendedVersions.JOOQ_VERSION
val flywayVersion = RecommendedVersions.FLYWAY_VERSION
val mysqlVersion: String by project

plugins {
    java
    idea
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
//    id("org.jooq.jooq-codegen-gradle") version "3.19.30"  // jooq 공식 플러그인, but 아직 트렌드는 아님
//    id("nu.studer.jooq") version "9.0"                // jooq plugin (test-container 적용 안됨)
    id("dev.monosoul.jooq-docker") version "8.0.12"     // test-container 적용 가능한 jooq plugin
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "jooq-practice"

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
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
        annotationProcessor("org.projectlombok:lombok")
        compileOnly("org.projectlombok:lombok")

        testAnnotationProcessor("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")

        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:testcontainers-junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.jar {
        enabled = true
    }

    tasks.bootJar {
        enabled = false
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq") {
        exclude(group = "org.jooq", module = "jooq")
    }
    implementation("org.jooq:jooq:${jooqVersion}")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    jooqCodegen(project(":jooq-custom"))
//    jooqCodegen("com.mysql:mysql-connector-j")
    jooqCodegen("org.jooq:jooq:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-meta:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-codegen:${jooqVersion}")
    jooqCodegen("org.flywaydb:flyway-core:${flywayVersion}")
    jooqCodegen("org.flywaydb:flyway-mysql:${flywayVersion}")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation("org.springframework.boot:spring-boot-starter-jooq-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}

jooq {
    version = jooqVersion
    withContainer {
        image {
            name = "mysql:$mysqlVersion"
            envVars = mapOf(
                "MYSQL_ROOT_PASSWORD" to "passwd",
                "MYSQL_DATABASE" to "sakila",
            )
        }

        db {
            username = "root"
            password = "passwd"
            name = "sakila"
            port = 3306
            jdbc {
                schema = "jdbc:mysql"
                driverClassName = "com.mysql.cj.jdbc.Driver"
            }
        }
    }
}

tasks {
    generateJooqClasses {
        schemas.set(listOf("sakila"))
        basePackageName.set("com.example.jooqpractice")
        outputDirectory.set(project.layout.buildDirectory.dir("generated/jooq"))
        includeFlywayTable.set(false)

        usingJavaConfig {
            generate = Generate()
                .withJavaTimeTypes(true)
                .withDeprecated(false)
                .withDaos(true)
                .withFluentSetters(true)
                .withRecords(true)

            withStrategy(
                Strategy().withName("com.example.jooq.custom.JPrefixGeneratorStrategy")
            )

            database.withForcedTypes(
                ForcedType()
                    .withUserType("java.lang.Long")
                    .withTypes("int unsigned"),
                ForcedType()
                    .withUserType("java.lang.Integer")
                    .withTypes("tinyint unsigned"),
                ForcedType()
                    .withUserType("java.lang.Integer")
                    .withTypes("smallint unsigned")
            )
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/jooq")
        }
    }
}

tasks.bootJar {
    enabled = true
}