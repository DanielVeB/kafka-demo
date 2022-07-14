package com.kuroszdaniel.demo.kafka

import com.kuroszdaniel.demo.config.KafkaStreamsConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.test.context.EmbeddedKafka
import java.time.Duration
import java.util.concurrent.Callable


@SpringBootTest(classes = [KafkaProcessor::class, KafkaStreamsConfig::class])
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    ],
    topics = ["testTopic", "testTopic2"]
)
@Import(KafkaTestUtil::class)
internal class KafkaProcessorIT {

    @Autowired
    lateinit var producer: KafkaProducer<String, Any>

    @Autowired
    lateinit var consumer: KafkaConsumer<String, Any>

    @Test
    fun `test`() {
        producer.send(ProducerRecord("testTopic", "key", "value"))

        consumer.subscribe(listOf("testTopic2"))
        var records = consumer.poll(Duration.ZERO)
        while (records.isEmpty) {
            records = consumer.poll(Duration.ZERO)
        }

        consumer.close()

        assertEquals("value", records.records("testTopic2").first().value())

    }
}