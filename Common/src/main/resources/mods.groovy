ModsDotGroovy.make {
    def modid = this.buildProperties["mod_id"]
    def majorForgeVersion = (this.libs.versions.forge as String).split("-")[1].split("\\.")[0]

    modLoader = "javafml"
    loaderVersion = "[${majorForgeVersion},)"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/trimmed/issues/"

    accessWidener = "trimmed.accesswidener"

    mod {
        modId = modid
        displayName = this.buildProperties["mod_name"]
        version = this.version
        group = this.group
        authors = [this.buildProperties["mod_author"] as String]

        displayUrl = "https://modrinth.com/mod/trimmed/"
        sourcesUrl = "https://github.com/dhyces/trimmed/"
        logoFile = "logo.png"
        description = "Better item overrides! Better trim support! Override it all!"

        onFabric {
            entrypoints {
                main = "dhyces.trimmed.FabricTrimmed"
                client = "dhyces.trimmed.FabricTrimmedClient"
            }
        }

        onQuilt {
            entrypoints {
                init = "dhyces.trimmed.QuiltTrimmed"
                client_init = "dhyces.trimmed.QuiltTrimmedClient"
            }
            intermediateMappings = "net.fabricmc:intermediary"
        }

        dependencies {
            onForge {
                minecraft = "${this.libs.versions.minecraft.range}"
                forge = "[${majorForgeVersion},)"
            }

            onFabric {
                minecraft = "${this.libs.versions.minecraft.range}"
                fabricloader = ">=${this.fabricLoaderVersion}"
                mod {
                    modId = 'fabric-api'
                    versionRange = '>=0.86.0'
                }
            }

            onQuilt {
                minecraft = "${this.libs.versions.minecraft.range}"
                quilt_loader = ">=${this.quiltLoaderVersion}"
                quilted_fabric_api = ">=${this.libs.versions.quilt.fabric}"
                quilt_base = ">=${this.libs.versions.quilt.qsl}"
            }
        }
    }

    onFabricAndQuilt {
        environment = "*"
        mixin = [
                modid + ".mixins.json"
        ]
    }

    onFabric {
        mixin = [
                modid + ".fabric.mixins.json"
        ]
    }
}