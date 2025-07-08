pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TaskMan"
include(":app")
include(":taskserver")
include(":core:network")
include(":core:theme")
include(":feature:home")
include(":feature:profile")
include(":feature:auth")
include(":feature:control")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":feature:search")
include(":core:ui")
include(":core:shared")
include(":core:sync")
include(":feature:settings")
