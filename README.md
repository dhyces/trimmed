# Trimmed API

Depends on: [Common Network](https://modrinth.com/mod/common-network/versions)

```
repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = "https://api.modrinth.com/maven"
            }
        }
        filter {
            includeGroup "maven.modrinth"
        }
    }
}
```

If you're using ML, go to the source of Common Network and use mysticdrew's maven to get separate common/fabric/forge and then use GH packages for Trimmed. Otherwise, there are two ways to get resources from Modrinth and both are shown below.

### Forge
```
dependencies {
    // Using slug and version number. Human readable, but can be changed at discretion of the author
    runtimeOnly "maven.modrinth:common-network:1.0.0+1.20-forge"
    implementation fg.deobf("maven.modrinth:trimmed:1.20-2.0.0")
}
```

### Fabric
```
dependencies {
    // Using project and version IDs. These have no meaning, but do not change
    modRuntimeOnly("maven.modrinth:HIuqnQpi:AlRsvRjn")
    modImplementation("maven.modrinth:trimmed:1.20-2.0.0")
}
```