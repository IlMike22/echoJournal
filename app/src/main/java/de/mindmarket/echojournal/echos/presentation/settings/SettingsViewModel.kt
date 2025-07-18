package de.mindmarket.echojournal.echos.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.mindmarket.echojournal.echos.domain.echo.EchoDataSource
import de.mindmarket.echojournal.echos.domain.echo.Mood
import de.mindmarket.echojournal.echos.domain.settings.SettingsPreferences
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val echoDataSource: EchoDataSource
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            observeSettings()
            observeTopicSearchResults()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingsState()
        )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeTopicSearchResults() {
        state
            .distinctUntilChangedBy { it.searchText }
            .map { it.searchText }
            .debounce(300)
            .flatMapLatest { query ->
                if (query.isNotBlank()) {
                    echoDataSource.searchTopics(query)
                } else emptyFlow()
            }
            .onEach { filteredResults ->
                _state.update {
                    val filteredNonDefaultResults = filteredResults - it.topics
                    val searchText = it.searchText.trim()
                    val isNewTopic =
                        searchText !in filteredNonDefaultResults && searchText !in it.topics && searchText.isNotBlank()
                    it.copy(
                        suggestedTopics = filteredNonDefaultResults,
                        isTopicSuggestionsVisible = filteredResults.isNotEmpty() || isNewTopic,
                        showCreateTopicOption = isNewTopic
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnAddButtonClick -> onAddButtonClick()
            is SettingsAction.OnSelectTopicClick -> onSelectTopicClick(action.topic)
            SettingsAction.OnDismissTopicDropDown -> onDismissTopicDropDown()
            is SettingsAction.OnMoodClick -> onMoodClick(action.mood)
            is SettingsAction.OnRemoveTopicClick -> onRemoveTopicClick(action.topic)
            is SettingsAction.OnSearchTextChanged -> onSearchTextChanged(action.text)
            SettingsAction.OnBackClick -> Unit
        }
    }

    private fun onSearchTextChanged(text: String) {
        _state.update { it.copy(searchText = text) }
    }

    private fun onDismissTopicDropDown() {
        _state.update {
            it.copy(
                isTopicSuggestionsVisible = false
            )
        }
    }

    private fun onAddButtonClick() {
        _state.update {
            it.copy(
                isTopicTextInputVisible = true
            )
        }
    }

    private fun onMoodClick(mood: MoodUi) {
        viewModelScope.launch {
            settingsPreferences.saveDefaultMood(Mood.valueOf(mood.name))
        }
    }

    private fun onSelectTopicClick(topic: String) {
        viewModelScope.launch {
            _state.update { it.copy(
                isTopicTextInputVisible = false,
                isTopicSuggestionsVisible = false,
                searchText = ""
            ) }

            val newDefaultTopics = (state.value.topics + topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun onRemoveTopicClick(topic: String) {
        viewModelScope.launch {
            val newDefaultTopics = (state.value.topics - topic).distinct()
            settingsPreferences.saveDefaultTopics(newDefaultTopics)
        }
    }

    private fun observeSettings() {
        combine(
            settingsPreferences.observeDefaultTopics(),
            settingsPreferences.observeDefaultMood()
        ) { topics, mood ->
            _state.update {
                it.copy(
                    topics = topics,
                    selectedMood = MoodUi.valueOf(mood.name)
                )
            }
        }.launchIn(viewModelScope)
    }
}