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

    // Documents
    const val DOCUMENTS = "documents"
    const val ADD_DOCUMENT = "documents/add"
    const val VIEW_DOCUMENT = "documents/view/{documentId}"
    const val EDIT_DOCUMENT = "documents/edit/{documentId}"
    fun viewDocument(documentId: Long) = "documents/view/$documentId"
    fun editDocument(documentId: Long) = "documents/edit/$documentId"

    // Logins
    const val LOGINS = "logins"
    const val ADD_LOGIN = "logins/add"
    const val VIEW_LOGIN = "logins/view/{loginId}"
    const val EDIT_LOGIN = "logins/edit/{loginId}"
    fun viewLogin(loginId: Long) = "logins/view/$loginId"
    fun editLogin(loginId: Long) = "logins/edit/$loginId"

    // Others
    const val OTHERS = "others"
    const val ADD_OTHER = "others/add"
    const val VIEW_OTHER = "others/view/{otherId}"
    const val EDIT_OTHER = "others/edit/{otherId}"
    fun viewOther(otherId: Long) = "others/view/$otherId"
    fun editOther(otherId: Long) = "others/edit/$otherId"
}