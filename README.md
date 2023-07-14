# Trimmed API

Depends on: [Common Network](https://modrinth.com/mod/common-network/versions), [MixinExtras 0.2.+](https://github.com/LlamaLad7/MixinExtras)

```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}
```

You will at least need a runtime dependency on common network and mixinextras


## Forge Only
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-common:0.2.0-beta.9")
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-forge:0.2.0-beta.9")
    // Using slug and version number. Human readable, but can be changed at discretion of the author
    runtimeOnly "maven.modrinth:common-network:1.0.1+1.20.1-forge"
    implementation fg.deobf("maven.modrinth:trimmed:1.20.1-2.1.0")
}
```

## Fabric Only
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:0.2.0-beta.9")
    // Using project and version IDs. These have no meaning, but do not change
    modRuntimeOnly("maven.modrinth:HIuqnQpi:9uXKGD5m")
    modImplementation("maven.modrinth:trimmed:1.20.1-2.1.0")
}
```

## MultiLoader
### Common
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-common:0.2.0-beta.9")
    implementation("mysticdrew:common-networking:1.0.1+1.20.1-common")
    implementation("dev.dhyces.trimmed:trimmed-common-1.20.1:2.1.0")
}
```
### Forge
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-forge:0.2.0-beta.9")
    implementation("mysticdrew:common-networking:1.0.1+1.20.1-forge")
    implementation fg.deobf("dev.dhyces.trimmed:trimmed-forge-1.20.1:2.1.0")
}
```
### Fabric
```groovy
dependencies {
    runtimeOnly("com.github.llamalad7.mixinextras:mixinextras-fabric:0.2.0-beta.9")
    modImplementation("mysticdrew:common-networking:1.0.1+1.20.1-fabric")
    modImplementation("dev.dhyces.trimmed:trimmed-fabric-1.20.1:2.1.0")
    
}
```