@file:Suppress("UnstableApiUsage")

import com.faendir.gradle.createWithBomSupport

plugins {
    id("com.faendir.gradle.bom-version-catalog") version "1.4.4"
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://kotlin.bintray.com/kotlinx")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        maven(url = "https://raw.githubusercontent.com/kotlin-graphics/mary/master")
    }
    versionCatalogs {
        createWithBomSupport("libs") {
            fromBomAlias("bom-lwjgl")
        }
    }
}

rootProject.name = "jacKot"
