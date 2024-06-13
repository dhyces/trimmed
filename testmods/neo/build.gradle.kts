plugins {
	idea
	java
	`maven-publish`
	alias(libs.plugins.curseforgegradle)
	alias(libs.plugins.minotaur)
	alias(libs.plugins.archloom)
}

base {
	archivesName = "${properties["mod_id"]}-neo-${libs.versions.minecraft.release.get()}"
}

val neoJava: Configuration by configurations.creating {
	isCanBeResolved = true
}
val neoResources: Configuration by configurations.creating {
	isCanBeResolved = true
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

loom {
	neoForge {
		accessTransformer(project(":neo").file("src/main/resources/META-INF/accesstransformer.cfg"))
	}

	runs {
		configureEach {
			ideConfigGenerated(true)
			runDir("run")
		}
		named("client") {
			client()
			configName = "Neo Test Client"
		}
		named("server") {
			server()
			configName = "Neo Test Server"
		}
		create("data") {
			data()
			configName = "Neo Test Data"

			programArgs("--mod", properties["mod_id"] as String, "--all", "--output", file("src/generated/resources/").path, "--existing", file("src/main/resources/").path)
		}
	}
}

repositories {
	mavenLocal()
	maven {
		name = "Neo"
		url = uri("https://maven.neoforged.net/releases")
	}
	maven {
		name = "Maven for PR #1076" // https://github.com/neoforged/NeoForge/pull/1076
		url = uri("https://prmaven.neoforged.net/NeoForge/pr1076")
		content {
			includeModule("net.neoforged", "neoforge")
		}
	}
}

dependencies {
	minecraft(libs.minecraft)
	neoForge(libs.neoforge)
	mappings(loom.layered() {
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${libs.versions.parchment.mc.get()}:${libs.versions.parchment.release.get()}@zip")
	})

	implementation("dev.dhyces.trimmed:trimmed-neo-1.20.6:2.1.4")
//	compileOnly(project(":common"))
//	compileOnly(project(":neo"))
//	neoJava(project(path = ":neo", configuration = "neoJava"))
//	neoResources(project(path = ":neo", configuration = "neoResources"))
}

tasks.named<JavaCompile>("compileJava") {
	dependsOn(neoJava)
}

tasks.processResources {
	dependsOn(neoResources)
}

publishing {
	publications {
		create<MavenPublication>("testMavenJava") {
			groupId = properties["maven_group"] as String
			artifactId = base.archivesName.get()
			version = properties["mod_version"] as String
			from(components["java"])
		}
	}
}