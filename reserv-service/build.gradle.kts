plugins {
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("io.konform:konform-jvm:0.2.0")

    implementation("org.postgresql:postgresql:42.2.14")
    implementation("org.flywaydb:flyway-core")
    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("org.slf4j:slf4j-api:1.7.30")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")

    testApi("org.junit.jupiter:junit-jupiter-api:5.5.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.0")

    testImplementation("io.rest-assured:rest-assured:4.2.0")
    testImplementation("io.rest-assured:kotlin-extensions:4.2.0")
    testImplementation("io.rest-assured:json-path:4.2.0")
    testImplementation("io.rest-assured:xml-path:4.2.0")
    testImplementation("io.kotest:kotest-assertions-core:4.1.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    test {
        useJUnitPlatform()
    }

    val deployPath = "../deploy/server/bin/"
    val jarName = "reserv-service.jar"
    register("copyJar", Copy::class){
        from(bootJar)
        rename ("reserv-service-${project.version}.jar", jarName)
        into(deployPath)
    }
    register("deleteJar", Delete::class){
        delete("$deployPath/$jarName")
    }

    build {
        finalizedBy(named("copyJar"))
    }
    clean {
        finalizedBy(named("deleteJar"))
    }
}

configurations {
    springBoot {
        mainClassName = "dgis.interview.cinema.AppKt"
    }
}
