package de.mindmarket.echojournal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import de.mindmarket.echojournal.app.navigation.NavigationRoot
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EchoJournalTheme {
                NavigationRoot(
                    navController = rememberNavController()
                )
            }
        }
    }
}