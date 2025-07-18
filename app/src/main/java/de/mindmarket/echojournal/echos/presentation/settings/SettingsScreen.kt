package de.mindmarket.echojournal.echos.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import de.mindmarket.echojournal.core.presentation.designsystem.theme.bgGradient
import de.mindmarket.echojournal.core.presentation.util.defaultShadow
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import de.mindmarket.echojournal.echos.presentation.settings.components.DefaultTopicSelectorCard
import de.mindmarket.echojournal.echos.presentation.settings.components.MoodCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreenRoot(
    onGoBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                SettingsAction.OnBackClick -> onGoBack()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(SettingsAction.OnBackClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = MaterialTheme.colorScheme.bgGradient)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MoodCard(
                selectedMood = state.selectedMood,
                onMoodClick = { onAction(SettingsAction.OnMoodClick(it)) },
                modifier = Modifier
                    .defaultShadow(shape = RoundedCornerShape(8.dp))
            )

            DefaultTopicSelectorCard(
                topics = state.topics,
                searchText = state.searchText,
                topicSuggestions = state.suggestedTopics,
                showCreateTopicOption = state.showCreateTopicOption,
                showSuggestionsDropDown = state.isTopicSuggestionsVisible,
                canInputText = state.isTopicTextInputVisible,
                onSearchTextChange = {
                    onAction(SettingsAction.OnSearchTextChanged(it))
                },
                onToggleCanInputText = {
                    onAction(SettingsAction.OnAddButtonClick)
                },
                onAddTopicClick = {
                    onAction(SettingsAction.OnSelectTopicClick(it))
                },
                onRemoveTopicClick = {
                    onAction(SettingsAction.OnRemoveTopicClick(it))
                },
                onDismissSuggestionsDropDown = {
                    onAction(SettingsAction.OnDismissTopicDropDown)
                },
                modifier = Modifier
                    .defaultShadow(shape = RoundedCornerShape(8.dp))
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    EchoJournalTheme {
        SettingsScreen(
            state = SettingsState(
                selectedMood = MoodUi.EXCITED
            ),
            onAction = {}
        )
    }
}