package de.mindmarket.echojournal.echos.presentation.echos.models

import de.mindmarket.echojournal.core.presentation.util.UiText
import de.mindmarket.echojournal.echos.presentation.models.EchoUi

data class EchoDaySection(
    val dateHeader: UiText,
    val echos: List<EchoUi>
)
