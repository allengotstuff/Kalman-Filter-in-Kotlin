
plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.kalmanfilter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation(project(":utility"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}