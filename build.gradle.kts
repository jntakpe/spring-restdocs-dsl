import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
}

group = "com.github.jntakpe"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    val springRestDocsVersion = "2.0.3.RELEASE"
    val junitVersion = "5.3.2"
    val spekVersion = "2.0.0-rc.1"
    val assertJVersion = "3.11.1"
    compile(kotlin("stdlib-jdk8"))
    compile("org.springframework.restdocs:spring-restdocs-core:$springRestDocsVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntime("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    withType<Test> {
        useJUnitPlatform {
            includeEngines = setOf("spek2")
        }
    }
}

