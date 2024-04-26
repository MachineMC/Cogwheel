plugins {
    id("java-library-convention")
    `maven-publish`
}

dependencies {
    implementation(project(":cogwheel-core"))
    implementation(libs.snakeyaml.engine)
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("https://repo.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "cogwheel-yaml"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
