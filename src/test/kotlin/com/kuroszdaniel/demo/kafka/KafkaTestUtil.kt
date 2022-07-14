package com.kuroszdaniel.demo.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class KafkaTestUtil {

    @Bean
    fun kafkaProducer(): KafkaProducer<String, Any> {
        return KafkaProducer<String, Any>(
            mapOf(
                "bootstrap.servers" to listOf("localhost:9092"),
                "key.serializer" to StringSerializer::class.java,
                "value.serializer" to StringSerializer::class.java,
                "acs" to 1
            )
        )
    }

    @Bean
    fun kafkaConsumer() = KafkaConsumer<String, Any>(
        mapOf(
            "bootstrap.servers" to listOf("localhost:9092"),
            "group.id" to "group-id",

            "key.deserializer" to StringDeserializer::class.java,
            "value.deserializer" to StringDeserializer::class.java,
            "auto.offset.reset" to "earliest",
            "enable.auto.commit" to false
        )
    )
}