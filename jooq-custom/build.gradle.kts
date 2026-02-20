import dev.monosoul.jooq.RecommendedVersions

val jooqVersion = RecommendedVersions.JOOQ_VERSION

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jooq:jooq-codegen:${jooqVersion}")

    runtimeOnly("com.mysql:mysql-connector-j")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}