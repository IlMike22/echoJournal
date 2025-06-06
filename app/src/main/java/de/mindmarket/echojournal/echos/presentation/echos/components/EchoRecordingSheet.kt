package de.mindmarket.echojournal.echos.presentation.echos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.mindmarket.echojournal.R
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme

private const val PRIMARY_BUTTON_BUBBLE_SIZE_DP = 128
private const val SECONDARY_BUTTON_SIZE_DP = 48

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EchoRecordingSheet(
    formattedRecordDuration:String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onCompleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        SheetContent(
            formattedRecordDuration = formattedRecordDuration,
            isRecording = isRecording,
            onDismiss = onDismiss,
            onPlayClick = onPlayClick,
            onPauseClick = onPauseClick,
            onCompleteRecording = onCompleteRecording
        )
    }
}

@Composable
fun SheetContent(
    formattedRecordDuration:String,
    isRecording: Boolean,
    onDismiss: () -> Unit,
    onPauseClick: () -> Unit,
    onPlayClick: () -> Unit,
    onCompleteRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryBubbleSize = PRIMARY_BUTTON_BUBBLE_SIZE_DP.dp
    val secondaryButtonSize = SECONDARY_BUTTON_SIZE_DP.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isRecording) stringResource(R.string.recording_your_memories)
                else stringResource(R.string.recording_paused),
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = formattedRecordDuration,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row() {}
    }
}

@Preview
@Composable
private fun SheetContentPreview() {
    EchoJournalTheme {
        SheetContent(
            formattedRecordDuration = "00:02:14",
            onPauseClick = {},
            onPlayClick = {},
            onCompleteRecording = {},
            isRecording = true,
            onDismiss = {}
        )
    }
}