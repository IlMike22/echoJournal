package de.mindmarket.echojournal.echos.presentation.create_echo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CreateEchoViewModel: ViewModel() {
    private val _state = MutableStateFlow<CreateEchoState>(CreateEchoState())
    val state = _state
        .onStart {

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = CreateEchoState()
        )

    fun onAction(action: CreateEchoAction) {
        when (action) {
            CreateEchoAction.OnSelectMoodClick -> onSelectMoodClick()
            CreateEchoAction.OnCancelClick -> TODO()
            CreateEchoAction.OnConfirmMood -> onConfirmMood()
            CreateEchoAction.OnDismissMoodSelector -> onDismissMoodSelector()
            is CreateEchoAction.OnMoodClick -> onMoodClick(action.moodUi)
            CreateEchoAction.OnCreateNewTopicClick -> TODO()
            CreateEchoAction.OnDismissTopicSuggestions -> TODO()
            CreateEchoAction.OnNavigateBackClick -> TODO()
            is CreateEchoAction.OnNoteTextChange -> TODO()
            CreateEchoAction.OnPauseAudioClick -> TODO()
            CreateEchoAction.OnPlayAudioClick -> TODO()
            is CreateEchoAction.OnRemoveTopicClick -> TODO()
            CreateEchoAction.OnSaveClick -> TODO()
            is CreateEchoAction.OnTitleTextChange -> TODO()
            is CreateEchoAction.OnTopicClick -> TODO()
            is CreateEchoAction.OnTopicTextChange -> TODO()
            is CreateEchoAction.OnTrackSizeAvailable -> TODO()
        }
    }

    private fun onSelectMoodClick() =
        _state.update { it.copy(showMoodSelector = true) }

    private fun onConfirmMood() =
        _state.update { it.copy(
            mood = it.selectedMood,
            canSaveEcho = it.title.isNotBlank(),
            showMoodSelector = false
        ) }

    private fun onDismissMoodSelector() =
        _state.update { it.copy(showMoodSelector = false) }

    private fun onMoodClick(moodUi: MoodUi) =
        _state.update { it.copy(selectedMood = moodUi) }
}