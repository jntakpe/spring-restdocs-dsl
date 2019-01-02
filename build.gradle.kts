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
    compile(kotlin("stdlib-jdk8"))
    compile("org.springframework.restdocs:spring-restdocs-core:$springRestDocsVersion")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
