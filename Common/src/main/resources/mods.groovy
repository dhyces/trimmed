MultiplatformModsDotGroovy.make {
    def modid = buildProperties["mod_id"]
    def majorForgeVersion = (libs.versions.forge as String).split("-")[1].split("\\.")[0]

    modLoader = "javafml"
    loaderVersion = "[${majorForgeVersion},)"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/trimmed/issues/"

    accessWidener = "trimmed.accesswidener"

    mod {
        modId = modid
        displayName = buildProperties["mod_name"]
        authors = [buildProperties["mod_author"] as String]
        version = environmentInfo.version

        displayUrl = "https://modrinth.com/mod/trimmed/"
        sourcesUrl = "https://github.com/dhyces/trimmed/"
        logoFile = "logo.png"
        description = "Better item overrides! Better trim support! Override it all!"

        onFabric {
            entrypoints {
                main = "dev.dhyces.trimmed.FabricTrimmed"
                client = "dev.dhyces.trimmed.FabricTrimmedClient"
            }
        }

        onQuilt {
            entrypoints {
                init = "dev.dhyces.trimmed.QuiltTrimmed"
                client_init = "dev.dhyces.trimmed.QuiltTrimmedClient"
            }
            intermediateMappings = "net.fabricmc:intermediary"
        }

        dependencies {
            onNeoForge {
                mod("neoforge") {
                    versionRange = "${libs.versions.get("neoforge_range")}"
                }
            }
            onForge {
                minecraft = "${libs.versions.get("minecraft_range")}"
                forge = "[${majorForgeVersion},)"
            }
            onFabric {
                minecraft = "${libs.versions.get("minecraft_range")}"
                fabricloader = ">=${libs.versions.get("fabric_loader")}"
                mod {
                    modId = 'fabric-api'
                    versionRange = ">=${(libs.versions.get("fabric_api") as String).split("\\+")[0]}"
                }
            }
            onQuilt {
                minecraft = "${libs.versions.get("minecraft_range")}"
                quilt_loader = ">=${this.quiltLoaderVersion}"
                quilted_fabric_api = ">=${libs.versions.quilt.fabric}"
                quilt_base = ">=${libs.versions.quilt.qsl}"
            }
        }
    }

    onFabric {
        environment = Environment.ANY
        mixins {
            mixin("${modid}.mixins.json")
        }
    }

    onFabric {
        mixins {
            mixin("${modid}.fabric.mixins.json")
        }
    }
}