import org.gradle.kotlin.dsl.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
    `java-library`
    `maven-publish`
    `version-catalog`
}

internal fun Project.versionCatalog(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

configure<JavaPluginExtension> {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

version = properties["mod_version"] as String

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_${properties["mod_name"]}" }
    }
    manifest {
        attributes(
            "Specification-Title"      to properties["mod_name"],
            "Specification-Vendor"     to properties["mod_author"],
            "Specification-Version"    to archiveVersion,
            "Implementation-Title"     to project.name,
            "Implementation-Version"   to archiveVersion,
            "Implementation-Vendor"    to properties["mod_author"],
            "Implementation-Timestamp" to OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
            "Timestamp"                to System.currentTimeMillis(),
            "Built-On-Java"            to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
            "Built-On-Minecraft"       to versionCatalog().findVersion("minecraft.release").get()
        )
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }

    maven {
        name = "BlameJared Maven (CrT / Bookshelf)"
        url = uri("https://maven.blamejared.com")
    }

    maven {
        url = uri("https://jm.gserv.me/repository/maven-public/")
    }

    maven { url = uri("https://jitpack.io/") }
}

dependencies {
    compileOnly(versionCatalog().findLibrary("jetbrains.annotations").get())
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 21
}

// Disables Gradle's custom module metadata from being published to maven. The
// metadata includes mapped dependencies which are not reasonably consumable by
// other mod developers.
//tasks.withType<GenerateModuleMetadata>().configureEach {
//    enabled = false
//}

if (hasProperty("publisher")) {
    publishing {
        repositories {
            maven {
                name = "Local"
                url  = uri("file://" + findProperty("local_maven"))
            }
            if (hasProperty("mavenUrl")) {
                maven {
                    name = "Maven"
                    url  = findProperty("mavenUrl")?.let { uri(it) }!!
                    credentials(PasswordCredentials::class) {
                        username = findProperty("mavenUsername")?.toString()
                        password = findProperty("mavenPassword")?.toString()
                    }
                }
            }
            if (hasProperty("trimmedGithubPackages")) {
                maven {
                    name = "GitHubPackages"
                    url  = uri(findProperty("trimmedGithubPackages")?.let { uri(it) }!!)
                    credentials {
                        username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String
                        password = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String
                    }
                }
            }
        }
    }
}