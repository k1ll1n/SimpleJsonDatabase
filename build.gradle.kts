import java.util.Base64

plugins {
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `maven-publish`
    `java-library`
    id("signing")
    id("tech.yanand.maven-central-publish") version "1.3.0"
}

group = "com.araksis"
version = "0.0.1a"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.18.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
java {
    withJavadocJar()
    withSourcesJar()
}

tasks.build {
    dependsOn("shadowJar")
}
val publicationName = "mavenKotlin"
publishing {
    publications {
        create<MavenPublication>(publicationName) {
            from(components["java"])
            artifactId = "sjd"
            pom {
                name.set("Simple Json Database (SJD)")
                description.set("Simple JSON Database (SJD) is a lightweight library for working with JSON files as a database.")
                url.set("https://github.com/k1ll1n/SimpleJsonDatabase")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/k1ll1n/SimpleJsonDatabase/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("k1ll1n")
                        name.set("k1ll1n")
                        email.set("subikrus@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/k1ll1n/SimpleJsonDatabase.git")
                    developerConnection.set("scm:git:ssh://git@github.com:k1ll1n/SimpleJsonDatabase.git")
                    url.set("https://github.com/k1ll1n/SimpleJsonDatabase")
                }
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications[publicationName])
}

mavenCentral {
    authToken = Base64.getEncoder().encodeToString(
        System.getenv("MAVEN_CENTRAL_TOKEN")?.toByteArray(Charsets.UTF_8)
            ?: byteArrayOf()
    )
    publishingType = "USER_MANAGED"
    maxWait = 60
}