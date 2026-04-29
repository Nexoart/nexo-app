

pluginManagement {
    repositories {
        google() // Sem filtros para não dar erro de "not found"
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

rootProject.name = "nexoapp"
include(":app")
 