package de.mindmarket.echojournal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import de.mindmarket.echojournal.echos.presentation.components.EchoExpandableText
import de.mindmarket.echojournal.echos.presentation.echos.EchosScreenRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoJournalTheme {
                EchoExpandableText(
                    text = buildString {
                            repeat(100) { append("Hello") }
                        }
                )
            }
        }
    }
}