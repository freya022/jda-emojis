import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    signing
    id("com.vanniktech.maven.publish") version "0.30.0"
    alias(libs.plugins.kotlin)
}

group = "dev.freya02"
version = "3.0.0_DEV"

val generateEmojisTask = tasks.register<GenerateEmojisTask>("generateEmojis") {
    outputs.upToDateWhen { false }
}

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
}

val targetJavaVersion = java.targetCompatibility.majorVersion
val jvmVersion = JavaVersion.current().majorVersion
tasks.withType<Javadoc> {
    isFailOnError = true

    options {
        this as StandardJavadocDocletOptions
        locale = "en"
        charSet = "UTF-8"
        encoding = "UTF-8"
        docTitle = "jda-emojis ${project.version}"
        windowTitle = "$docTitle Documentation"
        links(
            "https://docs.oracle.com/en/java/javase/$targetJavaVersion/docs/api",
            "https://docs.jda.wiki",
        )
//        isUse = true
        isSplitIndex = true

        // Always 9+
        addBooleanOption("html5", true)
        addStringOption("-release", targetJavaVersion)
        if (jvmVersion >= "23") {
            addBooleanOption("-no-fonts", true)
        }
    }
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
    api(libs.jspecify)
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

val mavenCentralUsername: String? by project
val mavenCentralPassword: String? by project
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
val canPublish = mavenCentralUsername != null && mavenCentralPassword != null && canSign

if (canPublish) {
    version = (version as String).replace("_DEV", "")

    signing {
        isRequired = canPublish

        useInMemoryPgpKeys(mavenGpgKeyId, mavenGpgSecretKey, "")
    }

    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

        signAllPublications()

        pom {
            description = "Brings emojis to your JDA projects"
            url = "https://github.com/freya022/jda-emojis"

            licenses {
                license {
                    name = "Apache License, Version 2.0"
                    url = "https://opensource.org/license/apache-2-0"
                    distribution = "repo"
                }
            }

            developers {
                developer {
                    name = "freya022"
                    email = "41875020+freya022@users.noreply.github.com"
                }
            }

            scm {
                connection = "scm:git:https://github.com/freya022/jda-emojis.git"
                developerConnection = "scm:git:https://github.com/freya022/jda-emojis.git"
                url = "https://github.com/freya022/jda-emojis"
            }
        }
    }
}