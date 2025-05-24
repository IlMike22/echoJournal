package de.mindmarket.echojournal.echos.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.mindmarket.echojournal.core.presentation.designsystem.theme.EchoJournalTheme
import de.mindmarket.echojournal.echos.presentation.models.MoodUi
import kotlin.random.Random

@Composable
fun EchoPlayBar(
    amplitudeBarWidth: Dp,
    amplitudeBarSpacing: Dp,
    powerRatios: List<Float>,
    trackColor: Color,
    trackFillColor: Color,
    playerProgress: () -> Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
    ) {
        val amplitudeBarWidthPx = amplitudeBarWidth.toPx()
        val amplitudeBarSpacingPx = amplitudeBarSpacing.toPx()

        val clipPath = Path()

        powerRatios.forEachIndexed { index,ratio ->
            val height = ratio * size.height
            val xOffset = index * (amplitudeBarSpacingPx + amplitudeBarWidthPx)
            val yTopStart = center.y - height / 2f

            val topLeft = Offset(
                x = xOffset,
                y = yTopStart
            )
            val rectSize =  Size(
                width = amplitudeBarWidthPx,
                height = height
            )

            val roundRect = RoundRect(
                rect = Rect(
                    offset = topLeft,
                    size = Size(
                        width = amplitudeBarWidthPx,
                        height = height
                    )
                ),
                cornerRadius = CornerRadius(100f)
            )
            clipPath.addRoundRect(roundRect)

            drawRoundRect(
                color = trackColor,
                topLeft = topLeft,
                size = rectSize,
                cornerRadius = CornerRadius(100f)
            )
        }
        clipPath(clipPath) {
            drawRect(
                color = trackFillColor,
                size = Size(
                    width = size.width * playerProgress(),
                    height = size.height
                )
            )
        }
    }
}

@Preview
@Composable
private fun EchoPlayBarPreview() {
    val ratios = remember {
        (1..30).map {
            Random.nextFloat()
        }
    }
    EchoJournalTheme {
        EchoPlayBar(
            amplitudeBarSpacing = 3.dp,
            amplitudeBarWidth = 4.dp,
            powerRatios = ratios,
            trackColor = MoodUi.SAD.colorSet.desaturated,
            trackFillColor = MoodUi.SAD.colorSet.vivid,
            playerProgress = {0.31f},
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}