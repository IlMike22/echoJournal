package de.mindmarket.echojournal.echos.presentation.echos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class EchosViewModel(): ViewModel() {
    private val _state = MutableStateFlow(EchosState())
    val state = _state.onStart {

    }.stateIn(
        scope = viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EchosState()
    )

    fun onAction(event: EchosAction) {
        when (event) {
            EchosAction.OnFabClick -> {}
            EchosAction.OnFabLongClick -> {}
            EchosAction.OnMoodChipClick -> {}
            is EchosAction.OnRemoveFilters -> {}
            EchosAction.OnTopicChipClick -> {}
            EchosAction.OnSettingsClick -> {}
            EchosAction.OnDismissMoodDropdown -> {}
            EchosAction.OnDismissTopicDropdown -> {}
            is EchosAction.OnFilterByMoodClick -> {}
            is EchosAction.OnFilterByTopicClick -> {}
        }
    }
}