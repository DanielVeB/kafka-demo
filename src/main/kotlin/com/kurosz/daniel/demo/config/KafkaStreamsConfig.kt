package com.kurosz.daniel.demo.config

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig.*
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration

@Configuration
@EnableKafka
@EnableKafkaStreams
open class KafkaStreamsConfig(
    @Value("\${kafka.streams.bootstrap-servers}")
    val server: String,

    @Value("\${kafka.streams.output-topic}")
    val outputTopic: String,

    @Value("\${kafka.streams.input-topic}")
    val inputTopic: String,

    @Value("\${kafka.streams.schema-registry}")
    val schemaRegistry: String
) {


    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    open fun kStreamConfig(): KafkaStreamsConfiguration {
        return KafkaStreamsConfiguration(
            mapOf(
                APPLICATION_ID_CONFIG to "test",
                BOOTSTRAP_SERVERS_CONFIG to server,
                DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
                DEFAULT_VALUE_SERDE_CLASS_CONFIG to GenericAvroSerde::class.java,
                DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to WallclockTimestampExtractor().javaClass.name,
                "schema.registry.url" to schemaRegistry
            )
        )
    }
}