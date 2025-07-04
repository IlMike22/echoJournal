package de.mindmarket.echojournal.core.database.echoTopicRelation

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import de.mindmarket.echojournal.core.database.echo.EchoEntity
import de.mindmarket.echojournal.core.database.topic.TopicEntity

@Entity(
    primaryKeys = ["echoId", "topic"]
)
data class EchoTopicCrossReference(
    val echoId: Int,
    val topic: String
)

data class EchoWithTopics(
    @Embedded val echo: EchoEntity,
    @Relation(
        parentColumn = "echoId",
        entityColumn = "topic",
        associateBy = Junction(EchoTopicCrossReference::class)
    )
    val topics: List<TopicEntity>
)