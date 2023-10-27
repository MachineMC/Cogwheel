plugins {
    id("cogwheel.library-conventions")
    `maven-publish`
}

dependencies {
    implementation(project(":cogwheel-core"))
    implementation(libs.google.gson)
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
            artifactId = "cogwheel-json"
            version = "1.0.0"
            from(components["java"])
        }
    }
}