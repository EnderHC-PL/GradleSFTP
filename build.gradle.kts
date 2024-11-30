plugins {
    id("java-gradle-plugin")
    id("maven-publish")
    id("checkstyle")
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

group = "pl.enderhc.sftp"
version = "1.0.0"

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation")
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

checkstyle {
    toolVersion = "10.20.2"
    maxWarnings = 0
}

gradlePlugin {
    plugins {
        create("sftpUploadPlugin") {
            id = "pl.enderhc.sftp"
            implementationClass = "pl.enderhc.sftp.SFTPUploadPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.github.mwiede:jsch:0.2.21")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name
            version = "${project.version}"

            pom {
                name.set("SFTP Upload Plugin")
                description.set("A Gradle plugin for uploading files to SFTP servers")
                url.set("https://github.com/EnderHC-PL/GradleSFTP")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("neziw")
                        name.set("neziw")
                        email.set("contact@neziw.ovh")
                    }
                }
                dependencies {
                    project.configurations.compileClasspath.get().dependencies.forEach { dependency -> create(dependency) }
                }
            }
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "enderhc-repo"
            url = uri(
                "https://repo.enderhc.pl/" + (if (project.version.toString()
                        .endsWith("SNAPSHOT")
                ) "snapshots" else "releases") + "/"
            )

            credentials {
                val usernameKey = "MAVEN_USERNAME"
                val passwordKey = "MAVEN_PASSWORD"
                username = if (env.isPresent(usernameKey)) env.fetch(usernameKey) else System.getenv(usernameKey)
                password = if (env.isPresent(passwordKey)) env.fetch(passwordKey) else System.getenv(passwordKey)
            }
        }
    }
}