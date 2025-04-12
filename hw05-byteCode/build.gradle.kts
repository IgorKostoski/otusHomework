import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation ("ch.qos.logback:logback-classic")
}

tasks {
    create<ShadowJar>("setterDemoJar") {
        archiveBaseName.set("setterDemo")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "org.example.aop.instrumentation.setter.SetterDemo",
                "Premain-Class" to "org.example.aop.instrumentation.setter.Agent"))
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    create<ShadowJar>("proxyDemoJar") {
        archiveBaseName.set("proxyDemo")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "org.example.aop.instrumentation.proxy.ProxyDemo",
                "Premain-Class" to "org.example.aop.instrumentation.proxy.Agent"))
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    create<ShadowJar>("summatorDemoJar") {
        archiveBaseName.set("summatorDemo")
        archiveVersion.set("")
        archiveClassifier.set("")
        manifest {
            attributes(mapOf("Main-Class" to "org.example.aop.instrumentation.changer.SummatorDemo",
                "Premain-Class" to "org.example.aop.instrumentation.changer.Agent"))
        }
        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.runtimeClasspath.get())
    }

    build {
        dependsOn("setterDemoJar", "proxyDemoJar", "summatorDemoJar")
    }
}
