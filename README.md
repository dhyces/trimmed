# Trimmed API

Depends on: [Common Network](https://modrinth.com/mod/common-network/versions), [MixinExtras 0.2.+](https://github.com/LlamaLad7/MixinExtras)

You will at least need a runtime dependency on common network and mixinextras


## Forge Only
```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
    maven { url = "https://jitpack.io/" }
}

dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-common:0.2.0-beta.9")
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-forge:0.2.0-beta.9")
    // Using slug and version number. Human readable, but can be changed at discretion of the author
    runtimeOnly "maven.modrinth:common-network:9uXKGD5m"
    implementation fg.deobf("maven.modrinth:trimmed:1.20.1-2.1.0")
}
```

## Fabric Only
```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
    maven { url = "https://jitpack.io/" }
}

dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:0.2.0-beta.9")
    // Using project and version IDs. These have no meaning, but do not change
    modRuntimeOnly("maven.modrinth:HIuqnQpi:GjLUIsDm")
    modImplementation("maven.modrinth:trimmed:1.20.1-2.1.0")
}
```

## MultiLoader
### Root
```groovy
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/dhyces/trimmed")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") ?: System.getenv("TOKEN")
        }
    }
    maven { url = "https://jitpack.io/" }
    maven {
        url = "https://jm.gserv.me/repository/maven-public/"
    }
}
```
### Common
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-common:0.2.0-beta.9")
    implementation("mysticdrew:common-networking-common:1.0.1-1.20.1")
    implementation("dev.dhyces.trimmed:trimmed-common-1.20.1:2.1.0")
}
```
### Forge
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-forge:0.2.0-beta.9")
    implementation("mysticdrew:common-networking-forge:1.0.1-1.20.1")
    implementation fg.deobf("dev.dhyces.trimmed:trimmed-forge-1.20.1:2.1.0")
}
```
### Fabric
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:0.2.0-beta.9")
    modImplementation("mysticdrew:common-networking-fabric:1.0.1-1.20.1")
    modImplementation("dev.dhyces.trimmed:trimmed-fabric-1.20.1:2.1.0")
    
}
```