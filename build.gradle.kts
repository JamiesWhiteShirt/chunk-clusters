import groovy.util.Eval
import org.gradle.api.tasks.JavaExec

buildscript {
    repositories {
        gradleScriptKotlin()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

plugins {
    application
}

apply {
    plugin("kotlin")
    plugin("idea")
}

application {
    mainClassName = "com.jamieswhiteshirt.chunkclusters.ChunkClustersKt"
}

val appArgs by project

tasks {
    "run"(JavaExec::class) {
        if (appArgs is String) {
            args(Eval.me(appArgs as String))
        }
    }
}

repositories {
    gradleScriptKotlin()
}

dependencies {
    compile(kotlinModule("stdlib"))
    compile(kotlinModule("reflect"))
    compile("net.sourceforge.argparse4j:argparse4j:0.7.0")
}
