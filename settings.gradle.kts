pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "Neo"
            url  = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "Fabric"
            url  = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "Forge"
            url  = uri("https://maven.minecraftforge.net/")
        }
        maven {
            name = "Quilt (Release)"
            url  = uri("https://maven.quiltmc.org/repository/release/")
        }
        maven {
            name = "Sponge Snapshots"
            url  = uri("https://repo.spongepowered.org/repository/maven-public/")
        }
        maven {
            url  = uri("https://maven.parchmentmc.org")
        }
        maven {
            name = "Architectury"
            url  = uri("https://maven.architectury.dev/")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "trimmed"
include("common", "neo", /*"forge", "forge:testmod",*/ "fabric") // "quilt"