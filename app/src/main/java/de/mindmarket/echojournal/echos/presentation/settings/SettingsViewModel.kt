package de.mindmarket.echojournal.echos.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(

) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun onAction(event:SettingsAction) {
        when (event) {
            SettingsAction.OnAddButtonClick -> {}
            SettingsAction.OnCreateTopicClick -> {}
            SettingsAction.OnDismissTopicDropDown -> {}
            is SettingsAction.OnMoodClick -> {}
            is SettingsAction.OnRemoveTopicClick -> {}
            is SettingsAction.OnSearchTextChanged -> {}
            SettingsAction.OnBackClick -> {}
        }
    }
}