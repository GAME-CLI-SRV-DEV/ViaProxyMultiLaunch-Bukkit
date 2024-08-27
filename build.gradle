plugins {
    id "java"
}

base {
    java.toolchain.languageVersion = JavaLanguageVersion.of(17)
    compileJava.options.encoding = compileTestJava.options.encoding = javadoc.options.encoding = "UTF-8"

    archivesName = project.maven_name ?: rootProject.maven_name
    group = project.maven_group ?: rootProject.maven_group
    version = project.maven_version ?: rootProject.maven_version
}

repositories {
    mavenCentral()
    maven {
        name = "viaversion"
        url = "https://repo.viaversion.com/"
    }
}

dependencies {
    compileOnly "net.raphimc:ViaProxy:3.3.3"
}

processResources {
    //Config file: "version: ${version}"
    inputs.properties(
            "version": project.maven_version
    )

    filesMatching("viaproxy.yml") {
        expand(
                "version": project.maven_version
        )
    }
}
