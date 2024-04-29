import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("enchantmentconfig.loader")
    id("fabric-loom") version "1.6-SNAPSHOT"
}

val fabric_loader_version: String by project
val fabric_version: String by project
val minecraft_version: String by project
val mod_id: String by project

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
}

loom {
    if (project(":common").file("src/main/resources/${mod_id}.accesswidener").exists()) {
        accessWidenerPath.set(project(":common").file("src/main/resources/${mod_id}.accesswidener"))
    }
    mixin {
        defaultRefmapName.set("${mod_id}.refmap.json")
    }
    mods {
        register(mod_id) {
            sourceSet(sourceSets["main"])
        }
        register(mod_id + "_test") {
            sourceSet(sourceSets["test"])
        }
    }
    runs {
        named("client") {
            client()
            setConfigName("Fabric Client")
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
            runDir("run")
        }
        named("server") {
            server()
            setConfigName("Fabric Server")
            setSource(sourceSets["test"])
            ideConfigGenerated(true)
            vmArgs("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true")
            runDir("run")
        }
    }
}

tasks {
    // Credits to Witixin.
    register<RemapJarTask>("remapCommonJar") {
        val jarTask = project(":common").getTasks().named<AbstractArchiveTask>("jar").get();
        dependsOn(jarTask)

        inputFile.convention(jarTask.archiveFile)
        archiveFileName.set(archiveFileName.get().replace(".jar", "-intermediary.jar"))
        targetNamespace.set("intermediary")

        remapperIsolation = true
    }

    register<RemapJarTask>("remapApiJar") {
        val jarTask = project(":common").getTasks().named<AbstractArchiveTask>("apiJar").get();
        dependsOn(jarTask)

        archiveClassifier = "api"

        inputFile.convention(jarTask.archiveFile)
        archiveFileName.set(archiveFileName.get().replace(".jar", "-intermediary.jar"))
        targetNamespace.set("intermediary")

        remapperIsolation = true
    }

    named<DefaultTask>("assemble").configure {
        dependsOn("remapCommonJar")
        dependsOn("remapApiJar")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenIntermediaryJava") {
            artifactId = "${mod_id}-intermediary"
            artifact(tasks["remapCommonJar"]) {
                builtBy(tasks["remapCommonJar"])
            }
            artifact(tasks["remapApiJar"]) {
                builtBy(tasks["remapApiJar"])
                classifier = "api"
            }
        }
    }
    repositories {
        maven {
            name = "Greenhouse"
            url = uri("https://maven.greenhouseteam.dev/releases")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}