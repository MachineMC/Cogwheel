plugins {
    id("java-library-convention")
    `maven-publish`
}

dependencies {
    implementation(project(":cogwheel-core"))
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
            artifactId = "cogwheel-properties"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
