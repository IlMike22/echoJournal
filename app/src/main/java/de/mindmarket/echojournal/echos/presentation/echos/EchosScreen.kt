package de.mindmarket.echojournal.echos.presentation.echos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import de.mindmarket.echojournal.core.presentation.designsystem.theme.bgGradient
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoFilterRow
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoRecordFloatingActionButton
import de.mindmarket.echojournal.echos.presentation.echos.components.EchosEmptyBackground
import de.mindmarket.echojournal.echos.presentation.echos.components.EchosTopBar

@Composable
fun EchosScreenRoot(
    viewModel: EchosViewModel = viewModel<EchosViewModel>()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    EchosScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun EchosScreen(
    state: EchosState,
    onAction: (EchosAction) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            EchoRecordFloatingActionButton(
                onClick = {
                    onAction(EchosAction.OnFabClick)
                }
            )
        },
        topBar = {
            EchosTopBar(
                onSettingsClick = {
                    onAction(EchosAction.OnSettingsClick)
                }
            )
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = MaterialTheme.colorScheme.bgGradient
                )
        ) {
            EchoFilterRow(
                moodChipContent = state.moodChipContent,
                hasActiveTopicFilters = state.hasActiveTopicFilters,
                hasActiveMoodFilters = state.hasActiveMoodFilters,
                selectedEchoFilterChip = state.selectedEchoFilterChip,
                topics = state.topics,
                moods = state.moods,
                topicChipTitle = state.topicChipTitle,
                onAction = onAction,
                modifier = Modifier
                    .fillMaxWidth()
            )
            when {
                state.isLoadingData -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                state.hasEchosRecorded.not()-> {
                    EchosEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Echos() {
    EchoJournalTheme {
        EchosScreen(
            state = EchosState(
                isLoadingData = false,
                hasEchosRecorded = false
            ),
            onAction = {}
        )
    }
}