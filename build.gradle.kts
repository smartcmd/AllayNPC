plugins {
    id("java-library")
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "me.daoge.allaynpc"
description = "NPC plugin for AllayMC"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allay {
    api = "0.23.0-SNAPSHOT"

    plugin {
        entrance = ".AllayNPC"
        authors += "daoge_cmd"
        website = "https://github.com/smartcmd/AllayNPC"
        dependency("PlaceholderAPI")
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    compileOnly(group = "org.allaymc", name = "papi", version = "0.2.0")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}