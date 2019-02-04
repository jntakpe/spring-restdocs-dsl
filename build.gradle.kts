import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.11"
    id("org.jetbrains.dokka") version "0.9.17"
    `build-scan`
    `maven-publish`
    signing
}

group = "com.github.jntakpe"
version = "0.5.0"

repositories {
    mavenLocal()
    mavenCentral()
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
    withType<DokkaTask> {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/javadoc"
        enabled = JavaVersion.current().isJava8
        reportUndocumented = false
    }
}
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

signing {
    sign(publishing.publications["mavenJava"])
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    setTermsOfServiceAgree("yes")
}
