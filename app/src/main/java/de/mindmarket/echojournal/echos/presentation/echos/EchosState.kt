package de.mindmarket.echojournal.echos.presentation.echos

import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable.Companion.asUnselectedItems
import de.mindmarket.echojournal.core.presentation.util.UiText
import de.mindmarket.echojournal.echos.presentation.echos.models.AudioCaptureMethod
import de.mindmarket.echojournal.echos.presentation.echos.models.EchoDaySection
import de.mindmarket.echojournal.echos.presentation.echos.models.EchoFilterChip
import de.mindmarket.echojournal.echos.presentation.echos.models.MoodChipContent
import de.mindmarket.echojournal.echos.presentation.echos.models.RecordingState
import de.mindmarket.echojournal.echos.presentation.models.EchoUi
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration

data class EchosState(
    val echos: Map<UiText, List<EchoUi>> = emptyMap(),
    val currentCaptureMethod: AudioCaptureMethod? = null,
    val recordingElapsedDuration: Duration = Duration.ZERO,
    val hasEchosRecorded: Boolean = false,
    val hasActiveTopicFilters: Boolean = false,
    val hasActiveMoodFilters: Boolean = false,
    val isLoadingData: Boolean = true,
    val recordingState: RecordingState = RecordingState.NOT_RECORDING,
    val moods: List<Selectable<MoodUi>> = emptyList(),
    val topics: List<Selectable<String>> = emptyList(),
    val moodChipContent: MoodChipContent = MoodChipContent(),
    val selectedEchoFilterChip: EchoFilterChip? = null,
    val topicChipTitle: UiText = UiText.StringResource(R.string.all_topics)
) {
    val echoDaySections = echos
        .toList()
        .map { (dateHeader, echos) ->
            EchoDaySection(dateHeader, echos)
        }

    val formattedRecordDuration:String
        get() {
            val minutes = (recordingElapsedDuration.inWholeMinutes % 60).toInt()
            val seconds = (recordingElapsedDuration.inWholeSeconds % 60).toInt()
            val centiSeconds = (recordingElapsedDuration.inWholeMilliseconds % 1000 / 10.0).roundToInt()

            return String.format(
                locale = Locale.US,
                format = "%02d:%02d:%02d",
                minutes, seconds, centiSeconds
            )
        }
}
