package com.aks.offvault.navigation

object NavRoutes {
    const val HOME = "home"

    // Generic section placeholder (Documents, Logins, Others until they get dedicated screens)
    const val SECTION = "section/{sectionId}"
    fun section(sectionId: String) = "section/$sectionId"

    // Cards
    const val CARDS = "cards"
    const val ADD_CARD = "cards/add"
    const val VIEW_CARD = "cards/view/{cardId}"
    const val EDIT_CARD = "cards/edit/{cardId}"
    fun viewCard(cardId: Long) = "cards/view/$cardId"
    fun editCard(cardId: Long) = "cards/edit/$cardId"
}