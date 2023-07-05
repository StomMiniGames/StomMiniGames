plugins {
    kotlin("jvm") version "1.8.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.simplymerlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("dev.hollowcube:minestom-ce:54e839e58a")
    implementation("com.github.SimplyMerlin:FSMChamp:v1.1.0")
}

application {
    mainClass.set("com.simplymerlin.minigameserver.MainKt")
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        archiveFileName.set("server-$version.jar")
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        archiveFileName.set("server-noshadow-$version.jar")
    }

    build { dependsOn(shadowJar) }
    distTar { duplicatesStrategy = DuplicatesStrategy.WARN }
    distZip { duplicatesStrategy = DuplicatesStrategy.WARN }
}