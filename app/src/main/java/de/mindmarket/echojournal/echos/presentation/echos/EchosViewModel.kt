package de.mindmarket.echojournal.echos.presentation.echos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable
import de.mindmarket.echojournal.core.presentation.util.UiText
import de.mindmarket.echojournal.echos.domain.audio.AudioPlayer
import de.mindmarket.echojournal.echos.domain.echo.EchoDataSource
import de.mindmarket.echojournal.echos.domain.recording.VoiceRecorder
import de.mindmarket.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import de.mindmarket.echojournal.echos.presentation.echos.models.EchoFilterChip
import de.mindmarket.echojournal.echos.presentation.echos.models.MoodChipContent
import de.mindmarket.echojournal.echos.presentation.echos.models.PlaybackState
import de.mindmarket.echojournal.echos.presentation.echos.models.RecordingState
import de.mindmarket.echojournal.echos.presentation.echos.models.TrackSizeInfo
import de.mindmarket.echojournal.echos.presentation.models.EchoUi
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import de.mindmarket.echojournal.echos.presentation.util.AmplitudeNormalizer
import de.mindmarket.echojournal.echos.presentation.util.toEchoUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class EchosViewModel(
    private val voiceRecorder: VoiceRecorder,
    private val audioPlayer: AudioPlayer,
    private val echosDataSource: EchoDataSource
) : ViewModel() {
    private val selectedMoodFilters = MutableStateFlow<List<MoodUi>>(emptyList())
    private val selectedTopicsFilters = MutableStateFlow<List<String>>(emptyList())
    private val audioTrackSizeInfo = MutableStateFlow<TrackSizeInfo?>(null)

    private val _eventChannel = Channel<EchosEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val playingEchoId = MutableStateFlow<Int?>(null)

    private val _state = MutableStateFlow(EchosState())
    val state = _state.onStart {
        observeFilters()
        observeEchos()
    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EchosState()
    )

    private val echos = echosDataSource
        .observeEchos()
        .onEach { echos ->
            _state.update {
                it.copy(
                    hasEchosRecorded = echos.isNotEmpty(),
                    isLoadingData = false
                )
            }
        }
        .combine(audioTrackSizeInfo) { echos, trackSizeInfo ->
            if (trackSizeInfo != null) {
                echos.map { echo ->

                    echo.copy(
                        audioAmplitudes = AmplitudeNormalizer.normalize(
                            sourceAmplitudes = echo.audioAmplitudes,
                            trackWidth = trackSizeInfo.trackWidth,
                            barWidth = trackSizeInfo.barWidth,
                            spacing = trackSizeInfo.spacing
                        )
                    )
                }
            } else echos
        }
        .flowOn(Dispatchers.Default)

    fun onAction(action: EchosAction) {
        when (action) {
            EchosAction.OnRecordFabClick -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.STANDARD
                    )
                }
            }

            EchosAction.OnRequestPermissionQuickRecording -> {
                requestAudioPermission()
                _state.update {
                    it.copy(
                        currentCaptureMethod = AudioCaptureMethod.QUICK
                    )
                }
            }

            EchosAction.OnRecordButtonLongClick -> {
                startRecording(captureMethod = AudioCaptureMethod.QUICK)
            }

            is EchosAction.OnRemoveFilters -> {
                when (action.filterType) {
                    EchoFilterChip.MOODS -> selectedMoodFilters.update { emptyList() }
                    EchoFilterChip.TOPICS -> selectedTopicsFilters.update { emptyList() }
                }
            }

            EchosAction.OnTopicChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.TOPICS
                    )
                }
            }

            EchosAction.OnMoodChipClick -> {
                _state.update {
                    it.copy(
                        selectedEchoFilterChip = EchoFilterChip.MOODS
                    )
                }
            }

            EchosAction.OnDismissMoodDropdown,
            EchosAction.OnDismissTopicDropdown -> {
                _state.update { it.copy(selectedEchoFilterChip = null) }
            }

            EchosAction.OnSettingsClick -> {}
            is EchosAction.OnFilterByMoodClick -> {
                toggleMoodFilter(action.moodUi)
            }

            is EchosAction.OnFilterByTopicClick -> {
                toggleTopicFilter(action.topic)
            }

            is EchosAction.OnPlayEchoClick -> onPlayEchoClick(action.echoId)
            EchosAction.OnPauseAudioClick -> audioPlayer.pause()
            EchosAction.OnResumeRecordingClick -> resumeRecording()
            is EchosAction.OnTrackSizeAvailable -> {
                audioTrackSizeInfo.update { action.trackSizeInfo }
            }

            EchosAction.OnAudioPermissionGranted -> {
                startRecording(captureMethod = AudioCaptureMethod.STANDARD)
            }

            EchosAction.OnPauseRecordingClick -> pauseRecording()
            EchosAction.OnCancelRecording -> cancelRecording()
            EchosAction.OnCompleteRecording -> stopRecording()
        }
    }

    private fun observeEchos() {
        combine(
            echos,
            playingEchoId,
            audioPlayer.activeTrack
        ) { echos, playingEchoId, activeTrack ->
            if (playingEchoId == null || activeTrack == null) {
                return@combine echos.map { it.toEchoUi() }
            }

            echos.map { echo ->
                if (echo.id == playingEchoId) {
                    echo.toEchoUi(
                        currentPlaybackDuration = activeTrack.durationPlayed,
                        playbackState = if (activeTrack.isPlaying) PlaybackState.PLAYING else PlaybackState.PAUSED
                    )
                } else echo.toEchoUi()
            }
        }
            .groupByRelativeDate()
            .onEach { groupedEchos ->
                _state.update {
                    it.copy(
                        echos = groupedEchos
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(viewModelScope)
    }

    private fun onPlayEchoClick(echoId: Int) {
        val selectedEcho = _state.value.echos.values.flatten().first { it.id == echoId }
        val activeTrack = audioPlayer.activeTrack.value
        val isNewEcho = playingEchoId.value != echoId
        val isSameEchoIsPlayingFromBeginning = echoId == playingEchoId.value && activeTrack != null
                && activeTrack.durationPlayed == Duration.ZERO

        when {
            isNewEcho || isSameEchoIsPlayingFromBeginning -> {
                playingEchoId.update { echoId }
                audioPlayer.stop()
                audioPlayer.play(
                    filePath = selectedEcho.audioFilePath,
                    onComplete = ::completePlayback
                )
            }

            else -> audioPlayer.resume()
        }
    }

    private fun completePlayback() {
        _state.update {
            it.copy(
                echos = it.echos.mapValues { (_, echos) ->
                    echos.map { echo ->
                        echo.copy(
                            playbackCurrentDuration = Duration.ZERO
                        )
                    }
                }
            )
        }
        playingEchoId.update { null }
    }

    private fun startRecording(captureMethod: AudioCaptureMethod) {
        _state.update {
            it.copy(
                recordingState = when (captureMethod) {
                    AudioCaptureMethod.STANDARD -> RecordingState.NORMAL_CAPTURE
                    AudioCaptureMethod.QUICK -> RecordingState.QUICK_CAPTURE
                }
            )
        }

        voiceRecorder.start()

        if (captureMethod == AudioCaptureMethod.STANDARD) {
            voiceRecorder
                .recordingDetails
                .distinctUntilChangedBy { it.duration }
                .map { it.duration }
                .onEach { duration ->
                    _state.update {
                        it.copy(
                            recordingElapsedDuration = duration
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun pauseRecording() {
        voiceRecorder.pause()
        _state.update {
            it.copy(
                recordingState = RecordingState.PAUSED
            )
        }
    }

    private fun resumeRecording() {
        voiceRecorder.resume()
        _state.update {
            it.copy(
                recordingState = RecordingState.NORMAL_CAPTURE
            )
        }
    }

    private fun cancelRecording() {
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING,
                currentCaptureMethod = null
            )
        }
        voiceRecorder.cancel()
    }

    private fun stopRecording() {
        voiceRecorder.stop()
        _state.update {
            it.copy(
                recordingState = RecordingState.NOT_RECORDING
            )
        }

        val recordingDetails = voiceRecorder.recordingDetails.value
        viewModelScope.launch {
            if (recordingDetails.duration < MIN_RECORD_DURATION) {
                _eventChannel.send(EchosEvent.RecordingTooShort)
            } else {
                _eventChannel.send(EchosEvent.OnDoneRecording(recordingDetails))
            }
        }
    }

    private fun toggleMoodFilter(moodUi: MoodUi) {
        selectedMoodFilters.update { selectedMoods ->
            if (moodUi in selectedMoods) {
                selectedMoods - moodUi
            } else selectedMoods + moodUi
        }
    }

    private fun toggleTopicFilter(topic: String) {
        selectedTopicsFilters.update { selectedTopics ->
            if (topic in selectedTopics) {
                selectedTopics - topic
            } else selectedTopics + topic
        }
    }

    private fun observeFilters() {
        combine(
            selectedTopicsFilters,
            selectedMoodFilters
        ) { selectedTopics, selectedMoods ->
            _state.update {
                it.copy(
                    topics = it.topics.map { selectableTopic ->
                        Selectable(
                            item = selectableTopic.item,
                            selected = selectedTopics.contains(selectableTopic.item)
                        )
                    },
                    moods = MoodUi.entries.map {
                        Selectable(
                            item = it,
                            selected = selectedMoods.contains(it)
                        )
                    },
                    hasActiveMoodFilters = selectedMoods.isNotEmpty(),
                    hasActiveTopicFilters = selectedTopics.isNotEmpty(),
                    topicChipTitle = selectedTopics.deriveTopicsToText(),
                    moodChipContent = selectedMoods.asMoodChipContent()
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun requestAudioPermission() = viewModelScope.launch {
        _eventChannel.send(EchosEvent.RequestAudioPermission)
    }

    private fun List<String>.deriveTopicsToText(): UiText {
        return when (size) {
            0 -> UiText.StringResource(R.string.all_topics)
            1 -> UiText.Dynamic(this.first())
            2 -> UiText.Dynamic("${this.first()}, ${this.last()}")
            else -> {
                val extraElementCount = size - 2
                UiText.Dynamic("${this.first()}, ${this[1]} +$extraElementCount")
            }
        }
    }

    private fun List<MoodUi>.asMoodChipContent(): MoodChipContent {
        if (this.isEmpty()) {
            return MoodChipContent()
        }
        val icons = this.map { it.iconSet.fill }
        val moodNames = this.map { it.title }

        return when (size) {
            1 -> MoodChipContent(
                iconsRes = icons,
                title = moodNames.first()
            )

            2 -> MoodChipContent(
                iconsRes = icons,
                title = UiText.Combined(
                    format = "%s, %s",
                    uiTexts = moodNames.toTypedArray()
                )
            )

            else -> {
                val extraElementCount = size - 2
                MoodChipContent(
                    iconsRes = icons,
                    title = UiText.Combined(
                        format = "%s, %s +$extraElementCount",
                        uiTexts = moodNames.take(2).toTypedArray()
                    )
                )
            }
        }
    }

    private fun Flow<List<EchoUi>>.groupByRelativeDate(): Flow<Map<UiText, List<EchoUi>>> {
        val formatter = DateTimeFormatter.ofPattern("dd MMM")
        val today = LocalDate.now()
        return map { echos ->
            echos
                .groupBy { echo ->
                    LocalDate.ofInstant(
                        echo.recordedAt,
                        ZoneId.systemDefault(),
                    )
                }
                .mapValues { (_, echos) ->
                    echos.sortedByDescending { it.recordedAt }
                }
                .toSortedMap(compareByDescending { it })
                .mapKeys { (date, _) ->
                    when (date) {
                        today -> UiText.StringResource(R.string.today)
                        today.minusDays(1) -> UiText.StringResource(R.string.yesterday)
                        else -> UiText.Dynamic(date.format(formatter))
                    }
                }
        }
    }

    companion object {
        private val MIN_RECORD_DURATION = 1.5.seconds
    }
}