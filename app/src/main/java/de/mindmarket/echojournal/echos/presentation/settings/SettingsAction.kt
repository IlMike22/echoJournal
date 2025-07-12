package de.mindmarket.echojournal.echos.presentation.settings

import de.mindmarket.echojournal.echos.presentation.models.MoodUi

sealed interface SettingsAction {
    data class OnSearchTextChanged(val text: String) : SettingsAction
    data object OnCreateTopicClick : SettingsAction
    data class OnRemoveTopicClick(val topic: String) : SettingsAction
    data object OnDismissTopicDropDown : SettingsAction
    data object OnAddButtonClick : SettingsAction
    data class OnMoodClick(val mood: MoodUi) : SettingsAction
    data object OnBackClick: SettingsAction
}