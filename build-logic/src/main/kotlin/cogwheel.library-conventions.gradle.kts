plugins {
    java
    `java-library`
}

group = "org.machinemc"
version = "1.0-SNAPSHOT"

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

repositories {
    mavenCentral()
    maven {
        url = uri("http://www.machinemc.org/releases")
        isAllowInsecureProtocol = true
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.engine)
    testImplementation(libs.junit.params)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
        options.compilerArgs = listOf("-Xlint:unchecked")
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    test {
        useJUnitPlatform()
    }
}