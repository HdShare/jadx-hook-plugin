import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	kotlin("jvm") version "2.0.0"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

kotlin {
	jvmToolchain(17)
}

group = "me.hd"
version = "1.0.2"

repositories {
	google()
	mavenCentral()
}

dependencies {
	compileOnly("io.github.skylot:jadx-core:1.5.2")
	implementation("com.highcapable.kavaref:kavaref-core:1.0.0")
	implementation("com.highcapable.kavaref:kavaref-extension:1.0.0")
	implementation("org.slf4j:slf4j-api:2.0.9")
}

tasks {
	val shadowJar = withType(ShadowJar::class) {
		archiveClassifier.set("")
		minimize()
	}

	register<Copy>("dist") {
		group = "jadx-plugin"
		dependsOn(shadowJar)
		dependsOn(withType(Jar::class))

		from(shadowJar)
		into(layout.buildDirectory.dir("dist"))
	}
}
