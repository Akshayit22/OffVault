package com.aks.offvault.navigation

object NavRoutes {
    const val HOME = "home"
    const val SECTION = "section/{sectionId}"

    fun section(sectionId: String) = "section/$sectionId"
}
