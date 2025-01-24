import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    `java-library`
    signing
    `maven-publish`
    alias(libs.plugins.kotlin)
}

group = "dev.freya02"
version = "1.0.0"

val generateEmojisTask = tasks.register<GenerateEmojisTask>("generateEmojis")

sourceSets {
    main {
        java {
            srcDir(generateEmojisTask)
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.jetbrains.annotations)
    api(libs.jda)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(kotlin("test"))
    testImplementation(kotlin("reflect"))
    testImplementation(libs.jemoji)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

val isCI = System.getProperty("GITHUB_ACTION") != null || System.getenv("GITHUB_ACTION") != null // GH actions
        || System.getProperty("GIT_COMMIT") != null || System.getenv("GIT_COMMIT") != null // Jitpack

val mavenUsername: String? by project
val mavenPassword: String? by project
/**
 * 1. Generate a key pair with `gpg --gen-key`, it will ask for a key name and an email address
 * 2. Start editing the key with `gpg --edit-key <key name>`, you should see a `gpg>` prompt
 * 3. (Optional) Modify the expiry of your primary key with `expire`
 * 4. If a subkey (`ssb`) was generated automatically, you can delete it by selecting it with `key 1` and running `delkey`
 * 5. Add a subkey with `addkey`, select `RSA (sign only)`
 * 6. Save everything with `save`, it will exit the gpg prompt
 * 7. Show the subkey IDs with `gpg -K --keyid-format short`, you should see two keys:
 *    - `sec ed25519/<primary key id> <created on> [SC]`, with the line below being the public key
 *    - `ssb rsa<length>/<subkey id> <created on> [S]`
 * 8. Set `mavenGpgKeyId` with the subkey id
 * 9. Set `mavenGpgSecretKey` with the secret key using `gpg --export-secret-key --armor <public key>`
 */
val mavenGpgKeyId: String? by project
val mavenGpgSecretKey: String? by project

val canSign = mavenGpgKeyId != null && mavenGpgSecretKey != null
val canPublish = mavenUsername != null && mavenPassword != null && canSign

publishing {
    publications {
        create<MavenPublication>("jda-emojis") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            url = URI("file:///C:/Users/freya02/Downloads/PublishingTest")
        }
    }
}

signing {
//    isRequired = canPublish

    useInMemoryPgpKeys(mavenGpgKeyId, mavenGpgSecretKey, "")
    sign(publishing.publications["jda-emojis"])
}