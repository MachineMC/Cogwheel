plugins {
    id("cogwheel.library-conventions")
    `maven-publish`
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("http://www.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
            isAllowInsecureProtocol = true
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "cogwheel-core"
            version = "1.1.0"
            from(components["java"])
        }
    }
}