plugins {
    kotlin("jvm") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

group = "com.simplymerlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    api("com.github.Minestom:Minestom:eb06ba8664")
    api("com.github.SimplyMerlin:FSMChamp:v1.0.3")
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    build { dependsOn(shadowJar) }
}