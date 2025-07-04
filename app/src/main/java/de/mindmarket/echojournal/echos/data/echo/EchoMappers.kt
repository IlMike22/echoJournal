package de.mindmarket.echojournal.echos.data.echo

import de.mindmarket.echojournal.core.database.echo.EchoEntity
import de.mindmarket.echojournal.core.database.echoTopicRelation.EchoWithTopics
import de.mindmarket.echojournal.core.database.topic.TopicEntity
import de.mindmarket.echojournal.echos.domain.echo.Echo
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

fun EchoWithTopics.toEcho(): Echo {
    return Echo(
        mood = echo.mood,
        title = echo.title,
        note = echo.note,
        topics = topics.map { it.topic },
        audioAmplitudes = echo.audioAmplitudes,
        audioFilePath = echo.audioFilePath,
        audioPlaybackLength = echo.audioPlaybackLength.milliseconds,
        recordedAt = Instant.ofEpochMilli(echo.recordedAt),
        id = echo.echoId
    )
}

fun Echo.toEchoWithTopics(): EchoWithTopics {
    return EchoWithTopics(
        echo = EchoEntity(
            echoId = id ?: 0,
            title = title,
            mood = mood,
            recordedAt = recordedAt.toEpochMilli(),
            note = note,
            audioFilePath = audioFilePath,
            audioAmplitudes = audioAmplitudes,
            audioPlaybackLength = audioPlaybackLength.inWholeMilliseconds
        ),
        topics = topics.map { TopicEntity(it) }
    )
}