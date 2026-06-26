package com.aks.offvault.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.aks.offvault.ui.about.AboutScreen
import com.aks.offvault.ui.data.DataScreen
import com.aks.offvault.ui.home.HomeScreen
import com.aks.offvault.ui.home.HomeViewModel
import com.aks.offvault.ui.logins.AddEditLoginDetailScreen
import com.aks.offvault.ui.logins.LoginDetailListScreen
import com.aks.offvault.ui.logins.LoginDetailViewModel
import com.aks.offvault.ui.logins.ViewLoginDetailScreen
import com.aks.offvault.ui.others.AddEditOtherScreen
import com.aks.offvault.ui.others.OtherListScreen
import com.aks.offvault.ui.others.OtherViewModel
import com.aks.offvault.ui.others.ViewOtherScreen
import com.aks.offvault.ui.section.SectionListScreen

@Composable
fun VaultNavGraph(
    homeViewModel: HomeViewModel,
    onLockClick: () -> Unit
) {
    val navController = rememberNavController()
    val cardViewModel: CardViewModel = viewModel()
    val documentViewModel: DocumentViewModel = viewModel()
    val loginDetailViewModel: LoginDetailViewModel = viewModel()
    val otherViewModel: OtherViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        enterTransition = {
            slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 3 } +
                    fadeIn(tween(300))
        },
        exitTransition = {
            slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 3 } +
                    fadeOut(tween(150))
        },
        popEnterTransition = {
            slideInHorizontally(tween(300, easing = FastOutSlowInEasing)) { -it / 3 } +
                    fadeIn(tween(300))
        },
        popExitTransition = {
            slideOutHorizontally(tween(300, easing = FastOutSlowInEasing)) { it / 3 } +
                    fadeOut(tween(150))
        }
    ) {

        // ── Home ────────────────────────────────────────────────────────────
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onSectionClick = { section ->
                    when (section.id) {
                        "cards" -> navController.navigate(NavRoutes.CARDS)
                        "documents" -> navController.navigate(NavRoutes.DOCUMENTS)
                        "logins" -> navController.navigate(NavRoutes.LOGINS)
                        "others" -> navController.navigate(NavRoutes.OTHERS)
                        else -> navController.navigate(NavRoutes.section(section.id))
                    }
                },
                onLockClick = onLockClick,
                onDataClick = { navController.navigate(NavRoutes.DATA) },
                onAboutClick = { navController.navigate(NavRoutes.ABOUT) }
            )
        }

        // ── Data ─────────────────────────────────────────────────────────────
        composable(NavRoutes.DATA) {
            DataScreen(onBackClick = { navController.popBackStack() })
        }

        // ── About ────────────────────────────────────────────────────────────
        composable(NavRoutes.ABOUT) {
            AboutScreen(onBackClick = { navController.popBackStack() })
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

        // ── Logins ──────────────────────────────────────────────────────────
        composable(NavRoutes.LOGINS) {
            LoginDetailListScreen(
                viewModel = loginDetailViewModel,
                onLoginDetailClick = { login -> navController.navigate(NavRoutes.viewLogin(login.id)) },
                onAddClick = { navController.navigate(NavRoutes.ADD_LOGIN) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ADD_LOGIN) {
            AddEditLoginDetailScreen(
                viewModel = loginDetailViewModel,
                editLoginDetailId = null,
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VIEW_LOGIN) { backStackEntry ->
            val loginId = backStackEntry.arguments?.getString("loginId")?.toLongOrNull()
                ?: return@composable
            ViewLoginDetailScreen(
                viewModel = loginDetailViewModel,
                loginDetailId = loginId,
                onEditClick = { navController.navigate(NavRoutes.editLogin(loginId)) },
                onDeleted = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EDIT_LOGIN) { backStackEntry ->
            val loginId = backStackEntry.arguments?.getString("loginId")?.toLongOrNull()
                ?: return@composable
            AddEditLoginDetailScreen(
                viewModel = loginDetailViewModel,
                editLoginDetailId = loginId,
                onSaved = {
                    navController.popBackStack(NavRoutes.LOGINS, inclusive = false)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Others ──────────────────────────────────────────────────────────
        composable(NavRoutes.OTHERS) {
            OtherListScreen(
                viewModel = otherViewModel,
                onOtherClick = { other -> navController.navigate(NavRoutes.viewOther(other.id)) },
                onAddClick = { navController.navigate(NavRoutes.ADD_OTHER) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ADD_OTHER) {
            AddEditOtherScreen(
                viewModel = otherViewModel,
                editOtherId = null,
                onSaved = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.VIEW_OTHER) { backStackEntry ->
            val otherId = backStackEntry.arguments?.getString("otherId")?.toLongOrNull()
                ?: return@composable
            ViewOtherScreen(
                viewModel = otherViewModel,
                otherId = otherId,
                onEditClick = { navController.navigate(NavRoutes.editOther(otherId)) },
                onDeleted = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.EDIT_OTHER) { backStackEntry ->
            val otherId = backStackEntry.arguments?.getString("otherId")?.toLongOrNull()
                ?: return@composable
            AddEditOtherScreen(
                viewModel = otherViewModel,
                editOtherId = otherId,
                onSaved = {
                    navController.popBackStack(NavRoutes.OTHERS, inclusive = false)
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}