package de.mindmarket.echojournal.echos.presentation.create_echo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.mindmarket.echojournal.app.navigation.NavigationRoute
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import de.mindmarket.echojournal.echos.domain.recording.RecordingStorage
import de.mindmarket.echojournal.echos.presentation.echos.models.TrackSizeInfo
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import de.mindmarket.echojournal.echos.presentation.util.AmplitudeNormalizer
import de.mindmarket.echojournal.echos.presentation.util.toRecordingDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateEchoViewModel(
    private val savedStateHandle: SavedStateHandle,
    val recordingStorage: RecordingStorage
) : ViewModel() {
    private val route = savedStateHandle.toRoute<NavigationRoute.CreateEcho>()
    private val recordingDetails = route.toRecordingDetails()

    private val eventChannel = Channel<CreateEchoEvent>()
    val events = eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow<CreateEchoState>(CreateEchoState())
    val state = _state
        .onStart {
            observeAddTopicText()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateEchoState()
        )


    fun onAction(action: CreateEchoAction) {
        when (action) {
            CreateEchoAction.OnSelectMoodClick -> onSelectMoodClick()
            CreateEchoAction.OnConfirmMood -> onConfirmMood()
            CreateEchoAction.OnDismissMoodSelector -> onDismissMoodSelector()
            is CreateEchoAction.OnMoodClick -> onMoodClick(action.moodUi)
            CreateEchoAction.OnDismissTopicSuggestions -> onDismissTopicSuggestions()
            is CreateEchoAction.OnNoteTextChange -> {}
            CreateEchoAction.OnPauseAudioClick -> {}
            CreateEchoAction.OnPlayAudioClick -> {}
            is CreateEchoAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            CreateEchoAction.OnSaveClick -> onSaveClick()
            is CreateEchoAction.OnTitleTextChange -> onTitleTextChangeClick(action.text)
            is CreateEchoAction.OnTopicClick -> onTopicClick(action.topic)
            is CreateEchoAction.OnTopicTextChange -> {}
            is CreateEchoAction.OnTrackSizeAvailable -> onTrackSizeAvailable(action.trackSizeInfo)
            is CreateEchoAction.OnAddTopicTextChange -> onAddTopicTextChange(action.text)
            CreateEchoAction.OnDismissConfirmLeaveDialog -> onConfirmDismissLeaveDialog()
            CreateEchoAction.OnGoBack,
            CreateEchoAction.OnNavigateBackClick,
            CreateEchoAction.OnCancelClick -> onShowConfirmLeaveDialog()
        }
    }

    private fun onTrackSizeAvailable(trackSizeInfo: TrackSizeInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val finalAmplitudes = AmplitudeNormalizer.normalize(
                sourceAmplitudes = recordingDetails.amplitudes,
                trackWidth = trackSizeInfo.trackWidth,
                barWidth = trackSizeInfo.barWidth,
                spacing = trackSizeInfo.spacing
            )
            _state.update { it.copy(
                playbackAmplitudes = finalAmplitudes
            ) }
        }
    }

    private fun onTitleTextChangeClick(text: String) {
        _state.update {
            it.copy(
                title = text
            )
        }
    }

    private fun onSaveClick() {
        if (recordingDetails.filePath == null) {
            return
        }
        viewModelScope.launch {
            val savedFilePath = recordingStorage.savePersistently(recordingDetails.filePath)
            if (savedFilePath == null) {
                eventChannel.send(CreateEchoEvent.FailedToSaveFile)
                return@launch
            }

            // TODO create Echo
        }

    }

    private fun onShowConfirmLeaveDialog() {
        _state.update {
            it.copy(
                showConfirmLeaveDialog = true
            )
        }
    }

    private fun onConfirmDismissLeaveDialog() =
        _state.update {
            it.copy(
                showConfirmLeaveDialog = false
            )
        }

    @OptIn(FlowPreview::class)
    private fun observeAddTopicText() {
        state
            .map { it.addTopicText }
            .distinctUntilChanged()
            .debounce(300)
            .onEach { query ->
                _state.update {
                    it.copy(
                        showTopicSuggestions = query.isNotBlank() && query.trim() !in it.topics,
                        searchResults = listOf("Hello", "TestMad").asUnselectedItems()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun onDismissTopicSuggestions() {
        _state.update {
            it.copy(
                showTopicSuggestions = false
            )
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        _state.update {
            it.copy(
                topics = it.topics - topic
            )
        }
    }

    private fun onTopicClick(topic: String) {
        _state.update {
            it.copy(
                addTopicText = "",
                topics = (it.topics + topic).distinct()
            )
        }
    }

    private fun onAddTopicTextChange(text: String) {
        _state.update {
            it.copy(
                addTopicText = text.filter {
                    it.isLetterOrDigit()
                }
            )
        }
    }

    private fun onSelectMoodClick() =
        _state.update { it.copy(showMoodSelector = true) }

    private fun onConfirmMood() =
        _state.update {
            it.copy(
                mood = it.selectedMood,
                canSaveEcho = it.title.isNotBlank(),
                showMoodSelector = false
            )
        }

    private fun onDismissMoodSelector() =
        _state.update { it.copy(showMoodSelector = false) }

    private fun onMoodClick(moodUi: MoodUi) =
        _state.update { it.copy(selectedMood = moodUi) }
}