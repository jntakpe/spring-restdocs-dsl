import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

val kotlinPoetVersion = "1.3.0"
val googleAutoServiceVersion = "1.0-rc6"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compile(project(":spring-restdocs-dsl-annotations"))
    compile(project(":spring-restdocs-dsl-core"))
    compile(kotlin("reflect"))
    compile(kotlin("stdlib-jdk8"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("com.google.auto.service:auto-service:$googleAutoServiceVersion")
    kapt("com.google.auto.service:auto-service:$googleAutoServiceVersion")
}

tasks { withType<KotlinCompile> { kotlinOptions { jvmTarget = "1.8" } } }

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn("dokka")
    archiveClassifier.set("javadoc")
    from(buildDir.resolve("javadoc"))
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
            pom {
                name.set(project.name)
                description.set("Kapt processor for generating a DSL based on spring-restdocs-dsl")
                url.set("https://github.com/jntakpe/spring-restdocs-dsl/spring-restdocs-dsl-processor")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("jntakpe")
                        name.set("Jocelyn NTAKPE")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:jntakpe/spring-restdocs-dsl.git")
                    developerConnection.set("scm:git:git@github.com:jntakpe/spring-restdocs-dsl.git")
                    url.set("https://github.com/jntakpe/spring-restdocs-dsl/")
                }
            }
        }
    }
    repositories {
        maven {
            fun repositoryUrl(): String {
                val repositoryBase = "https://oss.sonatype.org"
                return if (project.version.toString().endsWith("SNAPSHOT")) {
                    "$repositoryBase/content/repositories/snapshots"
                } else {
                    "$repositoryBase/service/local/staging/deploy/maven2"
                }
            }
            setUrl(repositoryUrl())
            credentials {
                val sonatypeUsername: String? by extra
                val sonatypePassword: String? by extra
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing { sign(publishing.publications["mavenJava"]) }