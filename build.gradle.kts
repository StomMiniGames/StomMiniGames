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
    implementation("dev.hollowcube:polar:1.3.0")
    
     // Logback
    implementation("ch.qos.logback:logback-core:1.4.5")
    implementation("ch.qos.logback:logback-classic:1.4.5")
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
        from(sourceSets.main.get().resources)
        duplicatesStrategy = DuplicatesStrategy.WARN
    }

    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        archiveFileName.set("server-noshadow-$version.jar")
    }

    named<JavaExec>("run") {
        environment("LOG_LEVEL", "DEBUG")
    }

    build { dependsOn(shadowJar) }
    distTar { duplicatesStrategy = DuplicatesStrategy.WARN }
    distZip { duplicatesStrategy = DuplicatesStrategy.WARN }
}