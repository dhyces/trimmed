import org.groovymc.modsdotgroovy.types.core.Platform
import org.groovymc.modsdotgroovy.gradle.tasks.AbstractGatherPlatformDetailsTask
import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
	idea
	java
	`maven-publish`
	alias(libs.plugins.mdg)
	alias(libs.plugins.curseforgegradle)
	alias(libs.plugins.minotaur)
	alias(libs.plugins.archloom)
	id("consumer.conventions")
}

base {
	archivesName = "${properties["mod_name"]}-neo-${libs.versions.minecraft.release.get()}"
}

val commonJava by configurations
val commonResources by configurations

sourceSets {
	val main = sourceSets.named("main").get()
	create("datagen") {
		compileClasspath += main.compileClasspath + main.output
		runtimeClasspath += main.runtimeClasspath + main.output
	}
}

loom {
	neoForge {
		accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))
	}

	runs {
		configureEach {
			ideConfigGenerated(true)
			runDir("run")
			mods {
				create("trimmed") {
					sourceSet(sourceSets.main.get())
					configuration(commonJava)
					configuration(commonResources)
				}
			}
		}
		named("client") {
			client()
			configName = "Neo Client"
		}
		named("server") {
			server()
			configName = "Neo Server"
		}
		create("data") {
			data()
			configName = "Neo Data"

			programArgs("--mod", properties["mod_id"] as String, "--all", "--output", project(":common").file("src/generated/resources/").path, "--existing", file("src/main/resources/").path)

			mods {
				named("trimmed") {
					sourceSet(sourceSets.main.get())
					sourceSet(sourceSets.named("datagen").get())
					configuration(commonJava)
					configuration(commonResources)
				}
			}
		}
	}
}

//remapJar {
//	atAccessWideners.add('src/main/resources/trimmed.accesswidener')
//}

repositories {
	maven {
		name = "Neo"
		url = uri("https://maven.neoforged.net/releases")
	}
}

dependencies {
	minecraft(libs.minecraft)
	neoForge(libs.neoforge)
	mappings(loom.layered() {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.mc.get()}:${libs.versions.parchment.release.get()}@zip")
	})
}


tasks.processResources {
	exclude("trimmed.accesswidener")
}

modsDotGroovy {
	platform(Platform.NEOFORGE)
	inferGather.set(false)
	multiplatform {
		from(":common")
	}
	apply()
}

tasks.named<AbstractGatherPlatformDetailsTask>("gatherNeoForgePlatformDetails").configure {
	minecraftVersion = libs.versions.minecraft.release
	platformVersion = libs.versions.neoforge.release
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = properties["maven_group"] as String
			artifactId = base.archivesName.get()
			version = properties["mod_version"] as String
			from(components["java"])
//			pom.withXml {
//				asNode().remove(asNode().dependencies)
//			}
		}
	}
}

if (hasProperty("modrinth_write_version_pat")) {
	modrinth {
		token = findProperty("modrinth_write_version_pat") as String
		projectId = properties["modrinth_project_id"] as String
		versionName = "NeoForge-${libs.versions.minecraft.release.get()}-${properties["mod_version"]}"
		versionNumber = "${libs.versions.minecraft.release.get()}-${properties["mod_version"]}+neoforge"
		versionType = properties["publish_type"] as String
		uploadFile.set(tasks.remapJar)
		gameVersions = libs.versions.publish.range.get().split(",")
		loaders = listOf("neoforge")
		changelog = rootProject.file("changelog.md").reader().use { it.readText() }
		additionalFiles = listOf(tasks.named("sourcesJar"), tasks.named("javadocJar"))
		detectLoaders = false
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
		mainFile.displayName = "NeoForge-${libs.versions.minecraft.release.get()}-${properties["mod_version"]}"
		mainFile.releaseType = properties["publish_type"]
		mainFile.changelog = rootProject.file("changelog.md").reader().use { it.readText() }
		mainFile.changelogType = "markdown"
		mainFile.addModLoader("NeoForge")
		mainFile.addJavaVersion("Java ${java.toolchain.languageVersion.get()}")
		mainFile.addGameVersion(libs.versions.minecraft.release.get())
		debugMode = properties["publish_debug"].toString().toBoolean()

		dependsOn(tasks.remapJar)
	}
}