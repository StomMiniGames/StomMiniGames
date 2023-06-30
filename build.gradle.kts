plugins {
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

tasks {
    jar {
        archiveFileName.set("server.jar")
    }
}

group = "com.simplymerlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:954e8b3915")
    implementation("com.github.SimplyMerlin:FSMChamp:v1.1.0")
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("com.simplymerlin.minigameserver.MainKt")
}

tasks {
    build { dependsOn(shadowJar) }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
}