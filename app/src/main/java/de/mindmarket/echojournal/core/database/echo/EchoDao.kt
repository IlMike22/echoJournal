package de.mindmarket.echojournal.core.database.echo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import de.mindmarket.echojournal.core.database.echoTopicRelation.EchoTopicCrossReference
import de.mindmarket.echojournal.core.database.echoTopicRelation.EchoWithTopics
import de.mindmarket.echojournal.core.database.topic.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EchoDao {
    @Query("SELECT * FROM echoentity ORDER BY recordedAt DESC")
    fun observeEchos(): Flow<List<EchoWithTopics>>

    @Query("SELECT * FROM topicentity ORDER BY topic ASC")
    fun observeTopics(): Flow<List<TopicEntity>>

    @Query("""
        SELECT * FROM topicentity WHERE topic LIKE "%" || :query || "%"
        ORDER BY topic ASC
    """)
    fun searchTopics(query:String): Flow<List<TopicEntity>>

    @Insert
    suspend fun insertEcho(echoEntity: EchoEntity): Long

    @Insert
    suspend fun upsertTopic(topicEntity: TopicEntity)

    @Insert
    suspend fun insertEchoTopicCrossReference(crossReference: EchoTopicCrossReference)

    @Transaction // reverts everything if anything gets wrong in that flow, needs to be completely successful
    suspend fun insertEchoWithTopics(echoWithTopics: EchoWithTopics) {
        val echoId = insertEcho(echoWithTopics.echo) // 1. insert new echo

        echoWithTopics.topics.forEach { topic ->
            upsertTopic(topic) // 2. insert new topic if does not exist yet
            insertEchoTopicCrossReference( // 3. insert the new reference between echo and topic
                crossReference = EchoTopicCrossReference(
                    echoId = echoId.toInt(),
                    topic = topic.topic
                )
            )
        }
    }

}