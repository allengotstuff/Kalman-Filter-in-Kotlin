plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.kalmanfilter.core"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.apache.commons:commons-math3:3.6.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}