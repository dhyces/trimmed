# Trimmed API

Depends on: [MixinExtras 0.3.2](https://github.com/LlamaLad7/MixinExtras)

## NeoForge Only
```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    implementation fg.deobf("maven.modrinth:trimmed:1.20.4-2.1.5+neoforge")
}
```

## Fabric Only
```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    modImplementation("maven.modrinth:trimmed:1.20.4-2.1.5+fabric")
}
```

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
    runtimeOnly("io.github.llamalad7:mixinextras-common:0.3.2")
    runtimeOnly("io.github.llamalad7:mixinextras-forge:0.3.2")
    implementation fg.deobf("maven.modrinth:trimmed:1.20.4-2.1.5+forge")
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
    runtimeOnly("io.github.llamalad7:mixinextras-common:0.3.2")
    implementation("dev.dhyces.trimmed:trimmed-common-1.20.4:2.1.5")
}
```
### Neo
```groovy
dependencies {
    implementation fg.deobf("dev.dhyces.trimmed:trimmed-neoforge-1.20.4:2.1.5")
}
```
### Fabric
```groovy
dependencies {
    modImplementation("dev.dhyces.trimmed:trimmed-fabric-1.20.4:2.1.5")
}
```
### Forge
```groovy
dependencies {
    runtimeOnly("io.github.llamalad7:mixinextras-forge:0.3.2")
    implementation fg.deobf("dev.dhyces.trimmed:trimmed-forge-1.20.4:2.1.5")
}
```