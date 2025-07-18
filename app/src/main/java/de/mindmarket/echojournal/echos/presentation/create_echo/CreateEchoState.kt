package de.mindmarket.echojournal.echos.presentation.create_echo

import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable
import de.mindmarket.echojournal.echos.presentation.echos.models.PlaybackState
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import kotlin.time.Duration

data class CreateEchoState(
    val title: String = "",
    val addTopicText: String = "",
    val topics: List<String> = listOf("Love", "Work"),
    val noteText: String = "",
    val showMoodSelector: Boolean = true,
    val selectedMood: MoodUi = MoodUi.NEUTRAL,
    val showTopicSuggestions: Boolean = false,
    val mood: MoodUi? = null,
    val searchResults: List<Selectable<String>> = emptyList(),
    val showCreateTopicOption: Boolean = true,
    val canSaveEcho: Boolean = false,
    val playbackAmplitudes: List<Float> = emptyList(),
    val playbackTotalDuration: Duration = Duration.ZERO,
    val playbackState: PlaybackState = PlaybackState.STOPPED,
    val durationPlayed: Duration = Duration.ZERO,
    val showConfirmLeaveDialog: Boolean = false
) {
    val durationPlayedRatio = (durationPlayed / playbackTotalDuration).toFloat()
}