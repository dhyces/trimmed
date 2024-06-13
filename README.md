# Trimmed API

## NeoForge Only
```groovy
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
    }
}

dependencies {
    implementation("maven.modrinth:trimmed:1.21-3.0.0+neoforge")
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
    modImplementation("maven.modrinth:trimmed:1.21-3.0.0+fabric")
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
    maven {
        url = "https://jm.gserv.me/repository/maven-public/"
    }
}
```
### Common
```groovy
dependencies {
    runtimeOnly("io.github.llamalad7:mixinextras-common:0.3.6")
    implementation("dev.dhyces.trimmed:trimmed-common-1.21-3.0.0")
}
```
### Neo
```groovy
dependencies {
    implementation("dev.dhyces.trimmed:trimmed-neoforge-1.21-3.0.0")
}
```
### Fabric
```groovy
dependencies {
    modImplementation("dev.dhyces.trimmed:trimmed-fabric-1.21-3.0.0")
}
```