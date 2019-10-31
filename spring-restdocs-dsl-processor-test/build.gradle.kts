import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

val junitVersion: String by extra
val spekVersion: String by extra
val assertJVersion: String by extra

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly(project(":spring-restdocs-dsl-annotations"))
    compileOnly(project(":spring-restdocs-dsl-core"))
    implementation(kotlin("stdlib-jdk8"))
    kapt(project(":spring-restdocs-dsl-processor"))
    testImplementation(project(":"))
    testImplementation(project(":spring-restdocs-dsl-core"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntime("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
}

tasks { withType<KotlinCompile> { kotlinOptions { jvmTarget = "1.8" } } }

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines = setOf("spek2")
    }
}

idea {
    module {
        val genDir = "build/generated/source"
        sourceDirs.addAll(files("$genDir/kapt/main", "$genDir/kaptKotlin/main"))
        generatedSourceDirs.addAll(files("$genDir/kapt/main", "$genDir/kaptKotlin/main"))
    }
}