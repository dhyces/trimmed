plugins {
    id("shared.conventions")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = true
}
val commonResources by configurations.creating {
    isCanBeResolved = true
}

dependencies {
    compileOnly(project(":Common"))
    commonJava(project(path = ":Common", configuration = "commonJava"))
    commonResources(project(path = ":Common", configuration = "commonResources"))
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.processResources {
    dependsOn(commonResources)
    from(commonResources)
    exclude("mods.groovy")
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava)
    dependsOn(commonResources)
    from(commonJava)
    from(commonResources)
}