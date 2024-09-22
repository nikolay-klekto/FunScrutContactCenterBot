
import nu.studer.gradle.jooq.JooqEdition
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.spring") version "1.7.10"
    id("nu.studer.jooq") version "7.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("org.jooq:jooq")
    runtimeOnly("org.postgresql:postgresql")
    jooqGenerator("org.postgresql:postgresql:42.3.5")
    implementation("io.r2dbc:r2dbc-postgresql:0.8.12.RELEASE")

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.telegram:telegrambots-spring-boot-starter:5.6.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.kohlschutter.junixsocket:junixsocket-core:2.6.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("com.kohlschutter.junixsocket:junixsocket-core:2.6.0")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("org.apache.httpcomponents:httpcore:4.4.14")
    implementation("org.json:json:20211205")
    implementation("com.github.docker-java:docker-java:3.2.13")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.2.13")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("funScrutContactCenterBot-${version}.jar")
}

//java.sourceSets["main"].java {
//    srcDir("src/generated/jooq")
//}

jooq {
    version.set("3.16.7")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://45.135.234.61:15432/fun_scrut"
                    user = "username"
                    password = "password"
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator" // Используем JavaGenerator вместо KotlinGenerator
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "contact_center_bot"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isFluentSetters = true
                        isPojos = true
                        withSequences(false)
                    }
                    target.apply {
                        packageName = "by.sf.bot.jooq"
                        directory = "src/generated/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}
