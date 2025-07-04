package de.mindmarket.echojournal.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.mindmarket.echojournal.core.database.echo.EchoDao
import de.mindmarket.echojournal.core.database.echo.EchoEntity
import de.mindmarket.echojournal.core.database.echo.FloatListTypeConverter
import de.mindmarket.echojournal.core.database.echo.MoodTypeConverter
import de.mindmarket.echojournal.core.database.echoTopicRelation.EchoTopicCrossReference
import de.mindmarket.echojournal.core.database.topic.TopicEntity

@Database(
    entities = [EchoEntity::class, TopicEntity::class, EchoTopicCrossReference::class],
    version = 1
)
@TypeConverters(
    MoodTypeConverter::class,
    FloatListTypeConverter::class
)
abstract class EchoDatabase : RoomDatabase() {
    abstract val echoDao: EchoDao
}