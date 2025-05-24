plugins {
    id("java")
}

group = "blps.labs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.keycloak:keycloak-core:26.2.0")
    implementation("org.keycloak:keycloak-server-spi:26.2.0")
    implementation("org.keycloak:keycloak-server-spi-private:26.2.0")
    implementation("org.keycloak:keycloak-model-jpa:26.2.0")
    implementation("org.keycloak:keycloak-services:26.2.0")



    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}