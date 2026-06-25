package com.aks.offvault.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aks.offvault.ui.home.HomeScreen
import com.aks.offvault.ui.home.HomeViewModel
import com.aks.offvault.ui.section.SectionListScreen

@Composable
fun VaultNavGraph(
    homeViewModel: HomeViewModel,
    onLockClick: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onSectionClick = { section ->
                    navController.navigate(NavRoutes.section(section.id))
                },
                onLockClick = onLockClick
            )
        }

        composable(NavRoutes.SECTION) { backStackEntry ->
            val sectionId = backStackEntry.arguments?.getString("sectionId") ?: return@composable
            val section = homeViewModel.sections.find { it.id == sectionId } ?: return@composable
            SectionListScreen(
                section = section,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}