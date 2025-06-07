package de.mindmarket.echojournal.echos.presentation.echos

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import de.mindmarket.echojournal.core.presentation.designsystem.theme.bgGradient
import de.mindmarket.echojournal.core.presentation.util.ObserveAsEvents
import de.mindmarket.echojournal.core.presentation.util.isAppInForeground
import de.mindmarket.echojournal.echos.presentation.echos.EchosAction.OnAudioPermissionGranted
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoFilterRow
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoList
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoQuickRecordFloatingActionButton
import de.mindmarket.echojournal.echos.presentation.echos.components.EchoRecordingSheet
import de.mindmarket.echojournal.echos.presentation.echos.components.EchosEmptyBackground
import de.mindmarket.echojournal.echos.presentation.echos.components.EchosTopBar
import de.mindmarket.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import de.mindmarket.echojournal.echos.presentation.echos.models.RecordingState
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun EchosScreenRoot(
    viewModel: EchosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && state.currentCaptureMethod == AudioCaptureMethod.STANDARD) {
            viewModel.onAction(OnAudioPermissionGranted)
        }

    }
    val context = LocalContext.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            EchosEvent.RequestAudioPermission -> {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            EchosEvent.OnDoneRecording -> {
                Timber.d("Recording successful!")
            }
            EchosEvent.RecordingTooShort -> {
                Toast.makeText(context, context.getString(R.string.audio_recording_was_too_short),
                Toast.LENGTH_LONG).show()
            }
        }
    }

    val isAppInForeground by isAppInForeground()
    LaunchedEffect(isAppInForeground, state.recordingState) {
        if (state.recordingState == RecordingState.NORMAL_CAPTURE && !isAppInForeground) {
            viewModel.onAction(EchosAction.OnPauseRecordingClick)
        }
    }

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
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            EchoQuickRecordFloatingActionButton(
                onClick = {
                    onAction(EchosAction.OnRecordFabClick)
                },
                isQuickRecording = state.recordingState == RecordingState.QUICK_CAPTURE,
                onLongPressEnd = { cancelledRecording ->
                    if (cancelledRecording) {
                        onAction(EchosAction.OnCancelRecording)
                    } else {
                        onAction(EchosAction.OnCompleteRecording)
                    }
                },
                onLongPressStart = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                    if (hasPermission) {
                        onAction(EchosAction.OnRecordButtonLongClick)
                    } else {
                        onAction(EchosAction.OnRequestPermissionQuickRecording)
                    }
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

                state.hasEchosRecorded.not() -> {
                    EchosEmptyBackground(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }

                else -> {
                    EchoList(
                        sections = state.echoDaySections,
                        onPlayClick = {
                            onAction(EchosAction.OnPlayEchoClick(it))
                        },
                        onPauseClick = {
                            onAction(EchosAction.OnPauseRecordingClick)
                        },
                        onTrackSizeAvailable = { trackSize ->
                            onAction(EchosAction.OnTrackSizeAvailable(trackSize))
                        }
                    )
                }
            }
        }

        if (state.recordingState in listOf(RecordingState.NORMAL_CAPTURE, RecordingState.PAUSED)) {
            EchoRecordingSheet(
                formattedRecordDuration = state.formattedRecordDuration,
                isRecording = state.recordingState == RecordingState.NORMAL_CAPTURE,
                onDismiss = { onAction(EchosAction.OnCancelRecording) },
                onPauseClick = { onAction(EchosAction.OnPauseRecordingClick) },
                onResumeClick = { onAction(EchosAction.OnResumeRecordingClick) },
                onCompleteRecording = { onAction(EchosAction.OnCompleteRecording) },
            )
        }
    }
}

@Composable
fun EchoList() {
    TODO("Not yet implemented")
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