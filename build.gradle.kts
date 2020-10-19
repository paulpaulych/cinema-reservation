
plugins {
    val kotlinVersion = "1.4.10"
    val springBootVersion = "2.3.4.RELEASE"
    val flywayVersion = "6.5.6"

    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    id("org.springframework.boot") version springBootVersion apply false
    id("org.flywaydb.flyway") version flywayVersion apply false
}


allprojects {
    /**
     * при изменении АПИ версию надо поднимать.
     * если обратная совместимость не нарушается, то минорное значение увеличивается на единицу.
     * иначе меняется второе значение, а минор обнуляется
     */

    version = "0.0.1"
    group = "io.github.paulpaulych.cinema-reservation"

    repositories {
        mavenCentral()
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/konform-kt/konform")
    }
}