MultiplatformModsDotGroovy.make {
    def modid = buildProperties["mod_id"]

    modLoader = "javafml"
    onNeoForge {
        loaderVersion = libs.versions.get("javafml_range")
    }

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/trimmed/issues/"

    onFabric {
        if (Boolean.parseBoolean(buildProperties["at_enabled"] as String)) {
            accessWidener = "${modid}.accesswidener"
        }
    }
    onNeoForge {
        accessTransformers {
            if (Boolean.parseBoolean(buildProperties["at_enabled"] as String)) {
                accessTransformer("META-INF/accesstransformer.cfg")
            }
        }
    }

    mod {
        modId = modid
        displayName = buildProperties["mod_name"]
        authors = [(buildProperties["authors"] as String)]
        version = environmentInfo.version

        displayUrl = "https://modrinth.com/mod/trimmed/"
        sourcesUrl = "https://github.com/dhyces/trimmed/"
        logoFile = "assets/trimmed/icon.png"
        description = "Better item overrides! Better trim support! Override it all!"

        onFabric {
            entrypoints {
                main = "dev.dhyces.trimmed.FabricTrimmed"
                client = "dev.dhyces.trimmed.FabricTrimmedClient"
            }
        }

        dependencies {
            onNeoForge {
                mod("neoforge") {
                    versionRange = "${libs.versions.get("neoforge_range")}"
                }
            }
            onFabric {
                minecraft = "${libs.versions.get("minecraft_range")}"
                fabricloader = ">=${libs.versions.get("fabric_loader")}"
                mod("fabric-api") {
                    versionRange = libs.versions.get("fabric_api_range")
                }
            }
        }
    }

    if (Boolean.parseBoolean(buildProperties["common_mixin_enabled"] as String)) {
        mixins {
            mixin("${modid}.mixins.json")
        }
    }

    onNeoForge {
        if (Boolean.parseBoolean(buildProperties["neoforge_mixin_enabled"] as String)) {
            mixins {
                mixin("${modid}.neo.mixins.json")
            }
        }
    }

    onFabric {
        environment = Environment.ANY
        if (Boolean.parseBoolean(buildProperties["fabric_mixin_enabled"] as String)) {
            mixins {
                mixin("${modid}.fabric.mixins.json")
            }
        }
    }
}