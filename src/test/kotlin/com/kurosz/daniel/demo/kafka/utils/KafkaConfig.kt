package com.kurosz.daniel.demo.kafka.utils

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer
import kafka.example.Cat
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils


@TestConfiguration
class KafkaConfig {

    fun configureConsumer(embeddedKafkaBroker: EmbeddedKafkaBroker): Consumer<Int, String> {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker)
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        return DefaultKafkaConsumerFactory<Int, String>(consumerProps)
            .createConsumer()
    }

    fun configureProducer(embeddedKafkaBroker: EmbeddedKafkaBroker): Producer<Int, String> {
        val producerProps: Map<String, Any> = HashMap(KafkaTestUtils.producerProps(embeddedKafkaBroker))
        return DefaultKafkaProducerFactory<Int, String>(producerProps).createProducer()
    }

    fun configureCatProducer(embeddedKafkaBroker: EmbeddedKafkaBroker): Producer<String, Cat> {
        val props = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to embeddedKafkaBroker.brokersAsString,
            ProducerConfig.BATCH_SIZE_CONFIG to "16384",
            ProducerConfig.LINGER_MS_CONFIG to 1,
            ProducerConfig.BUFFER_MEMORY_CONFIG to "33554432",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to SpecificAvroSerializer::class.java,
            "schema.registry.url" to "mock://localhost:1234"
        )
        return DefaultKafkaProducerFactory<String, Cat>(props).createProducer()
    }
}