package de.mindmarket.echojournal.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import de.mindmarket.echojournal.echos.presentation.create_echo.CreateEchoScreenRoot
import de.mindmarket.echojournal.echos.presentation.echos.EchosScreenRoot
import de.mindmarket.echojournal.echos.presentation.settings.SettingsScreenRoot
import de.mindmarket.echojournal.echos.presentation.util.toCreateEchoRoute

const val ACTION_CREATE_ECHO = "de.mind.market.CREATE_ECHO"

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoute.Echos(
            startRecording = false
        )
    ) {
        composable<NavigationRoute.Echos>(
            deepLinks = listOf(
                navDeepLink<NavigationRoute.Echos>(
                    basePath = "https://echojournal.com/echos"
                ) {
                    action = ACTION_CREATE_ECHO
                }
            )
        ) {
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