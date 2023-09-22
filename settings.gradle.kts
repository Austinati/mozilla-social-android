pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.mozilla.org/maven2") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://nexus.outadoc.fr/repository/public") }
        maven { url = uri("https://maven.mozilla.org/maven2") }
    }
}
rootProject.name = "Mozilla Social"
include(":app")
include(":core:designsystem")
include(":core:common")
include(":core:network")
include(":core:data")
include(":feature:auth")
include(":core:datastore")
include(":feature:settings")
include(":feature:feed")
include(":core:model")
include(":feature:post")
include(":feature:search")
include(":core:ui")
include(":feature:account")
include(":core:domain")
include(":core:database")
include(":feature:thread")
include(":feature:report")
include(":feature:hashtag")
include(":core:analytics")
