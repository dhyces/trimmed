ModsDotGroovy.make {
    def modid = this.buildProperties["mod_id"]
    def majorForgeVersion = (this.buildProperties["forge_version"] as String).split("\\.")[0]

    modLoader = "javafml"
    loaderVersion = "[${majorForgeVersion},)"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/trimmed/issues/"

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
            accessWidener = "trimmed.accesswidener"
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
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                forge = "[${majorForgeVersion},)"
            }

            onFabric {
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                fabricloader = ">=${this.fabricLoaderVersion}"
            }

            onQuilt {
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                quilt_loader = ">=${this.quiltLoaderVersion}"
                quilted_fabric_api = ">=${this.buildProperties["quilted_fabric_version"]}"
                quilt_base = ">=${this.buildProperties["qsl_version"]}"
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