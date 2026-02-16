import org.gradle.kotlin.dsl.annotationProcessor

val jooqVersion: String by project

buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-mysql:10.20.1")
        classpath("org.testcontainers:testcontainers:1.21.4")
        classpath("org.testcontainers:mysql:1.21.4")
        classpath("com.mysql:mysql-connector-j:9.5.0")
    }
}

plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
//    id("org.jooq.jooq-codegen-gradle") version "3.19.30"  // jooq 공식 플러그인, but 아직 트렌드는 아님
    id("nu.studer.jooq") version "9.0"
    idea
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "jooq-practice"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
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

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq") {
        exclude(group = "org.jooq", module = "jooq")
    }

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    implementation("org.jooq:jooq:${jooqVersion}")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    jooqGenerator(project(":jooq-custom"))
//    jooqGenerator("com.mysql:mysql-connector-j")
    jooqGenerator("org.jooq:jooq:${jooqVersion}")
    jooqGenerator("org.jooq:jooq-meta:${jooqVersion}")

    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-jooq-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("sakila") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3306/sakila"
                    user = System.getenv("MYSQL_USER") ?: "admin"
                    password = System.getenv("DB_PASSWORD") ?: "admin"
                }

                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"   // Java jooq code generator

//                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    strategy.name = "com.example.jooq.custom.JPrefixGeneratorStrategy"

                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "sakila"
                        isUnsignedTypes = false
                        excludes = "flyway_schema_history"
                    }

                    generate.apply {
                        isDaos = true
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                        isJavaTimeTypes = true
                        isDeprecated = false

//                        isJpaAnnotations = true
//                        jpaVersion = "2.2"

//                        isValidationAnnotations = true

//                        isSpringAnnotations = true
//                        isSpringDao = true

                    }

                    target.apply {
                        packageName = "com.example.jooqpractice"
                        directory = "build/generated/jooq"
                    }
                }
            }
        }
    }
}


//tasks.named("generateJooq") {
//}