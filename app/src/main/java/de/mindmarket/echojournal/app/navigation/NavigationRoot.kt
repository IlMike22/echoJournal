package de.mindmarket.echojournal.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.mindmarket.echojournal.echos.presentation.create_echo.CreateEchoScreenRoot
import de.mindmarket.echojournal.echos.presentation.echos.EchosScreenRoot
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
                }
            )
        }

        composable<NavigationRoute.CreateEcho> {
            CreateEchoScreenRoot()
        }
    }
}