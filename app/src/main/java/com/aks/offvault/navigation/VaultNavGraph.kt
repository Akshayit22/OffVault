package com.aks.offvault.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aks.offvault.ui.cards.AddEditCardScreen
import com.aks.offvault.ui.cards.CardListScreen
import com.aks.offvault.ui.cards.CardViewModel
import com.aks.offvault.ui.cards.ViewCardScreen
import com.aks.offvault.ui.documents.AddEditDocumentScreen
import com.aks.offvault.ui.documents.DocumentListScreen
import com.aks.offvault.ui.documents.DocumentViewModel
import com.aks.offvault.ui.documents.ViewDocumentScreen
import com.aks.offvault.ui.home.HomeScreen
import com.aks.offvault.ui.home.HomeViewModel
import com.aks.offvault.ui.section.SectionListScreen

@Composable
fun VaultNavGraph(
    homeViewModel: HomeViewModel,
    onLockClick: () -> Unit
) {
    val navController = rememberNavController()
    val cardViewModel: CardViewModel = viewModel()
    val documentViewModel: DocumentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {

        // ── Home ────────────────────────────────────────────────────────────
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onSectionClick = { section ->
                    when (section.id) {
                        "cards" -> navController.navigate(NavRoutes.CARDS)
                        "documents" -> navController.navigate(NavRoutes.DOCUMENTS)
                        else -> navController.navigate(NavRoutes.section(section.id))
                    }
                },
                onLockClick = onLockClick
            )
        }

        // ── Generic section placeholder ─────────────────────────────────────
        composable(NavRoutes.SECTION) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val section = homeViewModel.sections.find { it.id == sectionId } ?: return@composable
            SectionListScreen(
                section = section,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Cards ───────────────────────────────────────────────────────────
        composable(NavRoutes.CARDS) {
            CardListScreen(
                viewModel = cardViewModel,
                onCardClick = { card -> navController.navigate(NavRoutes.viewCard(card.id)) },
                onAddClick = { navController.navigate(NavRoutes.ADD_CARD) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ADD_CARD) {
            AddEditCardScreen(
                viewModel = cardViewModel,
                editCardId = null,
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VIEW_CARD) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")?.toLongOrNull()
                ?: return@composable
            ViewCardScreen(
                viewModel = cardViewModel,
                cardId = cardId,
                onEditClick = { navController.navigate(NavRoutes.editCard(cardId)) },
                onDeleted = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EDIT_CARD) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getString("cardId")?.toLongOrNull()
                ?: return@composable
            AddEditCardScreen(
                viewModel = cardViewModel,
                editCardId = cardId,
                onSaved = {
                    // Pop edit screen AND view screen, land back on the card list
                    navController.popBackStack(NavRoutes.CARDS, inclusive = false)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Documents ───────────────────────────────────────────────────────
        composable(NavRoutes.DOCUMENTS) {
            DocumentListScreen(
                viewModel = documentViewModel,
                onDocumentClick = { doc -> navController.navigate(NavRoutes.viewDocument(doc.id)) },
                onAddClick = { navController.navigate(NavRoutes.ADD_DOCUMENT) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ADD_DOCUMENT) {
            AddEditDocumentScreen(
                viewModel = documentViewModel,
                editDocumentId = null,
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VIEW_DOCUMENT) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("documentId")?.toLongOrNull()
                ?: return@composable
            ViewDocumentScreen(
                viewModel = documentViewModel,
                documentId = docId,
                onEditClick = { navController.navigate(NavRoutes.editDocument(docId)) },
                onDeleted = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EDIT_DOCUMENT) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString("documentId")?.toLongOrNull()
                ?: return@composable
            AddEditDocumentScreen(
                viewModel = documentViewModel,
                editDocumentId = docId,
                onSaved = {
                    navController.popBackStack(NavRoutes.DOCUMENTS, inclusive = false)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}