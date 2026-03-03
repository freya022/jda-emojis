plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jemoji)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
}
