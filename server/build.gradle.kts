plugins {
    kotlin("jvm") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

tasks {
    jar {
        archiveFileName.set("blockparty.jar")
    }
}

group = "com.simplymerlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:eb06ba8664")
    implementation("com.github.SimplyMerlin:FSMChamp:v1.0.3")
    implementation(project(":core"))

    implementation(project(":blockparty"))
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