package com.kuroszdaniel.demo.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import java.time.Duration


@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    ],
    topics = ["testTopic", "testTopic2"]
)
internal class KafkaProcessorIT {

    @Test
    fun `test`() {
        val producer = KafkaProducer<String, Any>(
            mapOf(
                "bootstrap.servers" to listOf("localhost:9092"),
                "key.serializer" to StringSerializer::class.java,
                "value.serializer" to StringSerializer::class.java,
                "acs" to 1
            )
        )

        val consumer = KafkaConsumer<String, Any>(
            mapOf(
                "bootstrap.servers" to listOf("localhost:9092"),
                "group.id" to "group-id",

                "key.deserializer" to StringDeserializer::class.java,
                "value.deserializer" to StringDeserializer::class.java,
                "auto.offset.reset" to "earliest",
                "enable.auto.commit" to false
            )
        )

        producer.send(ProducerRecord("testTopic", "key", "value"))


        consumer.subscribe(listOf("testTopic2"))
        var records = consumer.poll(Duration.ZERO)
        while (records.isEmpty) {
            records = consumer.poll(Duration.ZERO)
        }
        consumer.close()

        assertEquals("VALUEdsadsa", records.records("testTopic2").first().value())

    }

}