plugins {
    id("enchantmentconfig.common")
}

val group: String by project
val mod_id: String by project

configurations {
    register("commonJava") {
        isCanBeResolved = true
    }
    register("commonResources") {
        isCanBeResolved = true
    }
    register("commonTestResources") {
        isCanBeResolved = true
    }
}

dependencies {
    compileOnly(project(":common", "commonApi"))
    compileOnly(project(":common")) {
        capabilities {
            requireCapability("$group:$mod_id")
        }
    }
    "commonJava"(project(":common", "commonJava"))
    "commonResources"(project(":common", "commonResources"))
    "commonTestResources"(project(":common", "commonTestResources"))
}

tasks {
    named<JavaCompile>("compileJava").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<ProcessResources>("processResources").configure {
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
    named<ProcessResources>("processTestResources").configure {
        dependsOn(configurations.getByName("commonTestResources"))
        from(configurations.getByName("commonTestResources"))
        from(configurations.getByName("commonTestResources"))
    }
    named<Javadoc>("javadoc").configure {
        dependsOn(configurations.getByName("commonJava"))
        source(configurations.getByName("commonJava"))
    }
    named<Jar>("sourcesJar").configure {
        dependsOn(configurations.getByName("commonJava"))
        from(configurations.getByName("commonJava"))
        dependsOn(configurations.getByName("commonResources"))
        from(configurations.getByName("commonResources"))
    }
}