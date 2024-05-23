import org.groovymc.modsdotgroovy.core.Platform
import org.groovymc.modsdotgroovy.gradle.tasks.AbstractGatherPlatformDetailsTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    idea
    java
    `maven-publish`
    alias(libs.plugins.mdg)
    alias(libs.plugins.archloom)
    alias(libs.plugins.curseforgegradle)
    alias(libs.plugins.minotaur)
    id("consumer.conventions")
}

base {
    archivesName = "${extra["mod_id"]}-fabric-${libs.versions.minecraft.release.get()}"
}

val commonJava by configurations
val commonResources by configurations

loom {
    accessWidenerPath = project(":Common").file("src/main/resources/trimmed.accesswidener")

    runs {
        named("client"){
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }

    mixin {
        defaultRefmapName = "${properties["mod_id"]}.refmap.json"
    }

    mods {
        create("trimmed") {
            sourceSet(sourceSets.main.get())
            configuration(commonJava)
            configuration(commonResources)
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.mc.get()}:${libs.versions.parchment.release.get()}@zip")
    })

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api) {
        exclude(module = "fabric-models-v0")
    }
}

idea {
    module {
        inheritOutputDirs = false
    }
}

modsDotGroovy {
    platform(Platform.FABRIC)
    inferGather.set(false)
    multiplatform {
        from(":Common")
    }
    apply()
}

tasks.named<AbstractGatherPlatformDetailsTask>("gatherFabricPlatformDetails").configure {
    minecraftVersion = libs.versions.minecraft.release
    platformVersion = libs.versions.fabric.loader
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = properties["maven_group"] as String
            artifactId = base.archivesName.get()
            version = properties["mod_version"] as String
            from(components["java"])
//            pom.withXml {
//                asNode().remove(asNode().dependencies)
//            }
        }
    }
}

if (hasProperty("modrinth_write_version_pat")) {
    modrinth {
        token.set(findProperty("modrinth_write_version_pat") as String)
        projectId.set(properties["modrinth_project_id"] as String)
        versionName.set("Fabric-${libs.versions.minecraft.release.get()}-${properties["mod_version"]}")
        versionNumber.set("${libs.versions.minecraft.release.get()}-${properties["mod_version"]}+fabric")
        versionType.set(properties["publish_type"] as String)
        uploadFile.set(tasks.remapJar)
        gameVersions.set(libs.versions.publish.range.get().split(","))
        loaders.set(listOf("fabric"))
        changelog = rootProject.file("changelog.md").reader().use { it.readText() }
        additionalFiles.set(listOf(tasks.named("sourcesJar"), tasks.named("javadocJar")))
        detectLoaders.set(false)
        debugMode = properties["publish_debug"].toString().toBoolean()
    }
}

if (hasProperty("curseforge_publishing_token")) {
    tasks.register<TaskPublishCurseForge>("curseforge") {
        group = "publishing"

        disableVersionDetection()
        apiToken = findProperty("curseforge_publishing_token")
        val projectId = properties["curseforge_project_id"]
        val mainFile = upload(projectId, tasks.remapJar)
        mainFile.displayName = "Fabric-${libs.versions.minecraft.release.get()}-${properties["mod_version"]}"
        mainFile.releaseType = properties["publish_type"]
        mainFile.changelog = rootProject.file("changelog.md").reader().use { it.readText() }
        mainFile.changelogType = "markdown"
        mainFile.addModLoader("Fabric")
        mainFile.addJavaVersion("Java ${java.toolchain.languageVersion.get()}")
        mainFile.addGameVersion(libs.versions.minecraft.release.get())
        debugMode = properties["publish_debug"].toString().toBoolean()

        dependsOn(tasks.remapJar)
    }
}