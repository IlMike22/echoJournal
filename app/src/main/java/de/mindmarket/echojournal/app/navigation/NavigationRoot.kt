package de.mindmarket.echojournal.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.mindmarket.echojournal.echos.presentation.create_echo.CreateEchoScreenRoot
import de.mindmarket.echojournal.echos.presentation.echos.EchosScreenRoot
import de.mindmarket.echojournal.echos.presentation.settings.SettingsScreenRoot
import de.mindmarket.echojournal.echos.presentation.util.toCreateEchoRoute

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Echos
    ) {
        composable<NavigationRoute.Echos> {
            EchosScreenRoot(
                onNavigateToCreateEcho = { details ->
                    navController.navigate(details.toCreateEchoRoute())
                },
                onNavigateToSettings = {
                    navController.navigate(NavigationRoute.Settings)
                }
            )
        }

        composable<NavigationRoute.CreateEcho> {
            CreateEchoScreenRoot(
                onConfirmLeave = navController::navigateUp
            )
        }

        composable<NavigationRoute.Settings> {
            SettingsScreenRoot(
                onGoBack = navController::navigateUp
            )
        }
    }
}