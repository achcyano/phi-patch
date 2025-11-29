package me.achqing.phipatch.core

object Const {
    const val REPOSITORY_URL = "https://github.com/achcyano/phi-patch"
    const val APP_NAME = "PhiPatch"
    val UPDATE_API_URL = REPOSITORY_URL.replace("github.com", "api.github.com/repos") + "/releases/latest"
}