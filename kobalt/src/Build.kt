import com.beust.kobalt.*
import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.kotlin.*
//import com.beust.kobalt.plugin.dokka.*

val kotlin_version = "1.0.0-beta-4583"
val repos = repos("http://repository.jetbrains.com/all", "https://oss.sonatype.org/content/repositories/snapshots/")

val p = kotlinProject {

    name = "wasabi"
    group = "com.example"
    artifactId = name
    version = "0.1"

    sourceDirectoriesTest {
        path("test")
    }

    dependencies {
        compile("org.jetbrains.kotlin:kotlin-stdlib:" + kotlin_version)
        compile("org.jetbrains.kotlin:kotlin-reflect:" + kotlin_version)
        compile("com.fasterxml.jackson.core:jackson-core:2.6.4")
        compile("com.fasterxml.jackson.core:jackson-databind:2.6.4")
        compile("com.fasterxml.jackson.core:jackson-annotations:2.6.4")
        compile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.6.3")
        compile("com.fasterxml.woodstox:woodstox-core:5.0.1")

        compile("io.netty:netty-all:4.0.31.Final")
        compile("commons-codec:commons-codec:1.6")
        compile("commons-logging:commons-logging:1.1.1")
        compile("org.slf4j:slf4j-api:1.7.5")
        compile("joda-time:joda-time:2.3")
    }

    dependenciesTest {
        compile("junit:junit:4.9")
        compile("org.jetbrains.kotlin:kotlin-test:" + kotlin_version)
        compile("org.slf4j:slf4j-simple:1.7.5")
        compile("org.mockito:mockito-all:1.9.5")
        compile("org.apache.httpcomponents:httpcore:4.3.3")
        compile("org.apache.httpcomponents:httpclient:4.5.1")
    }

    assemble {
        jar {
            fatJar=true
            name = "wasabi-fat-" + version + ".jar"
        }
        mavenJars{
        }
    }

/*
    dokka {
        outputFormat = "markdown"
    }
*/
}
