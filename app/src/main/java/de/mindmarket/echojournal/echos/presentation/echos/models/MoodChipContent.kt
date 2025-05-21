package de.mindmarket.echojournal.echos.presentation.echos.models

import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.util.UiText

data class MoodChipContent(
    val iconsRes: List<Int> = emptyList(),
    val title: UiText = UiText.StringResource(R.string.all_moods)
)
