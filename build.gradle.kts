import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress(
    "DSL_SCOPE_VIOLATION",
    "MISSING_DEPENDENCY_CLASS",
    "UNRESOLVED_REFERENCE_WRONG_RECEIVER",
    "FUNCTION_CALL_EXPECTED"
)
plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runtime)
}

group = "net.fish"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://plugins.gradle.org/m2/")
    maven(url = "https://kotlin.bintray.com/kotlinx")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    maven(url = "https://raw.githubusercontent.com/kotlin-graphics/mary/master")
}

val lwjglNatives = "natives-" + if (Os.isFamily(Os.FAMILY_WINDOWS)) "windows" else "linux"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(platform(libs.bom.lwjgl))

    implementation(libs.jna.lib)
    implementation(libs.jna.jnajack)
    implementation(libs.jna.audioservers.jack)

    implementation(libs.gfx.lwjgl.core)
    implementation(libs.gfx.lwjgl.glfw)
    implementation(libs.gfx.lwjgl.nanovg)
    implementation(libs.gfx.lwjgl.opengl)
    implementation(libs.gfx.lwjgl.par)
    implementation(libs.gfx.lwjgl.stb)
    implementation(libs.gfx.lwjgl.jemalloc)

    runtimeOnly(variantOf(libs.gfx.lwjgl.core) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.glfw) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.nanovg) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.opengl) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.par) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.stb) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.gfx.lwjgl.jemalloc) { classifier(lwjglNatives) })

    implementation(libs.gfx.kotlin.graphics.gln)
    implementation(libs.gfx.kotlin.graphics.glm)
    implementation(libs.gfx.kotlin.graphics.unocore)
    implementation(libs.gfx.joml)

    implementation(libs.gfx.imgui.core)
    implementation(libs.gfx.imgui.gl)
    implementation(libs.gfx.imgui.glfw)

    implementation(libs.logging.logback.classic)
    implementation(libs.logging.jul2slf4j)
    implementation(libs.logging.logstash)

    testImplementation(libs.jupiter.engine)
    testImplementation(libs.jupiter.api)
    testImplementation(libs.jupiter.params)
    testImplementation(libs.assertj)
    testImplementation(libs.mockk)

}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "7.6.1"
        distributionType = Wrapper.DistributionType.ALL
    }

    named<ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "11"
            javaParameters = true
            freeCompilerArgs = freeCompilerArgs + listOf("-Xinline-classes", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

    withType(JavaExec::class.java) {
        jvmArgs = listOf("-Xmx1g")
    }
}

application {
    mainClass.set("MainKt")
}