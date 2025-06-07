package de.mindmarket.echojournal.echos.presentation.echos

import de.mindmarket.echojournal.echos.presentation.echos.models.EchoFilterChip
import de.mindmarket.echojournal.echos.presentation.echos.models.TrackSizeInfo
import de.mindmarket.echojournal.echos.presentation.models.MoodUi

sealed interface EchosAction {
    data object OnMoodChipClick : EchosAction
    data object OnDismissMoodDropdown : EchosAction
    data class OnFilterByMoodClick(val moodUi: MoodUi) : EchosAction
    data object OnTopicChipClick : EchosAction
    data object OnDismissTopicDropdown : EchosAction
    data class OnFilterByTopicClick(val topic: String) : EchosAction
    data object OnRecordFabClick : EchosAction
    data object OnRequestPermissionQuickRecording : EchosAction
    data object OnRecordButtonLongClick : EchosAction
    data object OnSettingsClick : EchosAction
    data class OnRemoveFilters(val filterType: EchoFilterChip) : EchosAction
    data class OnPlayEchoClick(val echoId: Int) : EchosAction
    data object OnPauseRecordingClick : EchosAction
    data object OnResumeRecordingClick: EchosAction
    data object OnCompleteRecording: EchosAction
    data object OnPauseAudioClick : EchosAction
    data class OnTrackSizeAvailable(val trackSizeInfo: TrackSizeInfo) : EchosAction
    data object OnAudioPermissionGranted: EchosAction
    data object OnCancelRecording: EchosAction
}