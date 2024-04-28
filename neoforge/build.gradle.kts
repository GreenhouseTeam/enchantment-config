import net.neoforged.gradle.dsl.common.runs.ide.extensions.IdeaRunExtension
import org.apache.tools.ant.filters.LineContains

plugins {
    id("enchantmentconfig.loader")
    id("net.neoforged.gradle.userdev") version "7.0.115"
}
jarJar.enable()

val at = file("src/main/resources/enchantmentconfig.cfg");
if (at.exists()) {
    minecraft.accessTransformers.file(at)
}

val mod_id: String by project
val neoforge_version: String by project

runs {
    configureEach {
        modSource(sourceSets["main"])
        modSource(sourceSets["test"])
        systemProperty("neoforge.enabledGameTestNamespaces", mod_id)
        extensions.configure<IdeaRunExtension>("idea") {
            primarySourceSet = sourceSets["test"]
        }
    }
    register("client") {
    }
    register("server") {
        programArgument("--nogui")
    }
}

dependencies {
    implementation("net.neoforged:neoforge:${neoforge_version}")
}

tasks {
    named<ProcessResources>("processResources").configure {
        filesMatching("*.mixins.json") {
            filter<LineContains>("negate" to true, "contains" to setOf("refmap"))
        }
    }
}

tasks {
    named<Jar>("jarJar").configure {
        archiveClassifier.set("")
    }
}