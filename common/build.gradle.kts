plugins {
    idea
    java
    `maven-publish`
    alias(libs.plugins.mdg)
    alias(libs.plugins.archloom)
    id("shared.conventions")
}

base {
    archivesName = "${properties["mod_id"]}-common-${libs.versions.minecraft.release.get()}"
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val testCompileOnly by configurations.named("testCompileOnly") {
    extendsFrom(configurations["compileOnly"])
}

loom {
    accessWidenerPath = file("src/main/resources/trimmed.accesswidener")

    mixin {
        defaultRefmapName = "trimmed.refmap.json"
    }

    runConfigs.configureEach {
        isIdeConfigGenerated = false
    }
}

modsDotGroovy {
    multiplatform {
        gather {
            projectProperty("mod_id")
            projectProperty("mod_name")
            projectProperty("mod_author")
        }
        expose()
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.mc.get()}:${libs.versions.parchment.release.get()}@zip")
    })

    modCompileOnly(libs.fabric.loader)

    annotationProcessor(libs.mixinextras.common.get())
    implementation(libs.mixinextras.common.get())
}

artifacts {
    add(commonJava.name, sourceSets.main.get().java.sourceDirectories.singleFile)
    add(commonResources.name, sourceSets.main.get().resources.sourceDirectories.singleFile)
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
    }
}

idea {
    module {
        inheritOutputDirs = false
    }
}

//import net.fabricmc.loom.task.AbstractRemapJarTask
//tasks.withType(AbstractRemapJarTask).each {
//    it.targetNamespace = "named"
//}

tasks.processResources {
    val buildProps = properties.toMap()

    filesMatching("pack.mcmeta") {
        expand(buildProps)
    }
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