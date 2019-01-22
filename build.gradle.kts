import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
    id("com.gradle.build-scan") version "2.1"
    `maven-publish`
    signing
}

group = "com.github.jntakpe"
version = "0.1.2-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set("Provides a convenient way to document and test APIs with Spring REST Docs leveraging Kotlin DSL")
                url.set("https://github.com/jntakpe/spring-restdocs-dsl")
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
            setUrl("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                val sonatypeUsername: String by extra
                val sonatypePassword: String by extra
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    setTermsOfServiceAgree("yes")
}
