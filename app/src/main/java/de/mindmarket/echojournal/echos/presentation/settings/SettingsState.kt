package de.mindmarket.echojournal.echos.presentation.settings

import de.mindmarket.echojournal.echos.presentation.models.MoodUi

data class SettingsState(
    val selectedMood: MoodUi? = null,
    val topics: List<String> = emptyList(),
    val searchText: String = "",
    val suggestedTopics: List<String> = emptyList(),
    val isTopicSuggestionsVisible: Boolean = false,
    val showCreateTopicOption: Boolean = false,
    val isTopicTextInputVisible: Boolean = false
)