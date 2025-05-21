package de.mindmarket.echojournal.echos.presentation.echos.components

import android.adservices.topics.Topic
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import de.mindmarket.echojournal.core.presentation.designsystem.chips.MultiChoiceChip
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.Selectable
import de.mindmarket.echojournal.core.presentation.designsystem.dropdowns.SelectableDropDownOptionsMenu
import de.mindmarket.echojournal.echos.presentation.echos.EchosAction
import de.mindmarket.echojournal.echos.presentation.echos.models.EchoFilterChip
import de.mindmarket.echojournal.echos.presentation.echos.models.MoodChipContent
import de.mindmarket.echojournal.echos.presentation.models.MoodUi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EchoFilterRow(
    moodChipContent: MoodChipContent,
    hasActiveMoodFilters: Boolean,
    selectedEchoFilterChip: EchoFilterChip?,
    moods: List<Selectable<MoodUi>>,
    hasActiveTopicFilters: Boolean,
    topics: List<Selectable<Topic>>,
    onAction: (EchosAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    FlowRow(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        MultiChoiceChip(
            displayText = moodChipContent.title.asString(),
            onClick = {
                onAction(EchosAction.OnMoodChipClick)
            },
            leadingContent = {
                if (moodChipContent.iconsRes.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy((-4).dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        moodChipContent.iconsRes.forEach { iconRes ->
                            Image(
                                imageVector = ImageVector.vectorResource(iconRes),
                                contentDescription = moodChipContent.title.asString(),
                                modifier = Modifier
                                    .height(16.dp)
                            )
                        }
                    }
                }
            },
            isClearVisible = hasActiveMoodFilters,
            isDropDownVisible = selectedEchoFilterChip == EchoFilterChip.MOODS,
            isHighlighted = hasActiveMoodFilters || selectedEchoFilterChip == EchoFilterChip.MOODS,
            onClearButtonClick = {
                onAction(EchosAction.OnRemoveFilters(EchoFilterChip.MOODS))
            },
            dropDownMenu = {
                SelectableDropDownOptionsMenu(
                    items = moods,
                    itemDisplayText = { moodUi -> moodUi.title.asString(context) },
                    onDismiss = {
                        onAction(EchosAction.OnDismissMoodDropdown)
                    },
                    key = {moodUi -> moodUi.title},
                    onItemClick = { moodUi ->
                        onAction(EchosAction.OnFilterByMoodClick(moodUi.item))
                    }
                )
            }
        )
    }
}