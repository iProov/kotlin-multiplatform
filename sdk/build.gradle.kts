import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinMultiplatform)
    id("maven-publish")
}

kotlin {
    android {
        namespace = "com.iproov.kmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    // Physical device (arm64)
    iosArm64().compilations.getByName("main") {
        cinterops.create("iproov") {
            definitionFile.set(project.file("src/nativeInterop/cinterop/cinterop.def"))
            compilerOpts(
                "-fmodules",
                "-framework",
                "IOSFramework",
                "-F${projectDir}/libs/ios/Device"
            )
        }
    }

    // Simulator (Apple Silicon)
    iosSimulatorArm64().compilations.getByName("main") {
        cinterops.create("iproov") {
            definitionFile.set(project.file("src/nativeInterop/cinterop/cinterop.def"))
            compilerOpts(
                "-fmodules",
                "-framework",
                "IOSFramework",
                "-F${projectDir}/libs/ios/Simulator"
            )
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.iproov.android)
        }

        commonMain.dependencies {
            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

group = "com.iproov.kmp"
version = libs.versions.iproov.sdk.version.get()

publishing {
    repositories {
        maven {
            url = uri(rootProject.extra["deployPath"] as String)
        }
    }
}