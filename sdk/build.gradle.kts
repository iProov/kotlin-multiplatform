import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinMultiplatform)
    id("maven-publish")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishAllLibraryVariants()
    }

    listOf(iosArm64()).forEach { iosTarget ->

        iosTarget.compilations.getByName("main") {
            val iproov by cinterops.creating {
                // Path to the .def file
                definitionFile.set(project.file("src/nativeInterop/cinterop/cinterop.def"))

                compilerOpts(
                    "-fmodules",
                    "-framework",
                    "IOSFramework",
                    "-F${projectDir}/libs/ios/ios-arm64"
                )
            }
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.startup.runtime)

            implementation(compose.ui)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.iproov.android)
        }

        commonMain.dependencies {
            // Compose
            implementation(compose.foundation)
            implementation(compose.material3)

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

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("iProov KMP")
                description.set("A KMP wrapper for the native iProov SDK.")
                url.set("https://github.com/iProov/kotlin-multiplatform-sdk")  // kmp wrapper repo url
            }
        }
    }
}