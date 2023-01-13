/*
 * Copyright 2020-2023 AeroService
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

defaultTasks("build", "shadowJar")

allprojects {
    group = "org.aero"
    version = "1.0.0"
    description = "A common core library"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    dependencies {
        implementation("org.jetbrains:annotations:24.0.0")
        implementation("org.slf4j:slf4j-api:2.0.5")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.1")
        testImplementation("org.junit.platform:junit-platform-suite-api:1.9.1")
        testRuntimeOnly("org.junit.platform:junit-platform-suite-engine:1.9.1")
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
        options.encoding = "UTF-8"
        options.isIncremental = true

    }

    publishing {
        publications {
            create<MavenPublication>(project.name) {
                from(components.findByName("java"))
            }
        }
    }
}
