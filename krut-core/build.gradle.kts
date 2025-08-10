plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("com.vanniktech.maven.publish")
    signing
}

group = "io.github.khudoyshukur"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:11.0.9")
}

tasks.test { useJUnitPlatform() }
kotlin { jvmToolchain(17) }

mavenPublishing {
    coordinates("io.github.khudoyshukur", "krut-core", "1.0.1")

    pom {
        name.set("Krut Core")
        description.set("Lightweight kotlin web framework")
        inceptionYear.set("2020")
        url.set("https://github.com/khudoyshukur/krut/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("khudoyshukur")
                name.set("Khudoyshukur")
                url.set("https://github.com/khudoyshukur/")
            }
        }
        scm {
            url.set("https://github.com/khudoyshukur/krut/")
            connection.set("scm:git:git://github.com/khudoyshukur/krut.git")
            developerConnection.set("scm:git:ssh://git@github.com/khudoyshukur/krut.git")
        }
    }
}