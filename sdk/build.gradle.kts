import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kotlinMultiplatform)
    id("maven-publish")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release", "debug")
    }

    // Physical device (arm64)
    iosArm64().compilations.getByName("main") {
        val iproov by cinterops.creating {
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
        val iproov by cinterops.creating {
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

android {
    namespace = group.toString()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
            groupId = "com.iproov.kmp"
            artifactId = "sdk"
            version = version.toString()
            from(components["kotlin"])
        }
    }
}