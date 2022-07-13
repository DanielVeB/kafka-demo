package com.kuroszdaniel.demo.config

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig.*
import org.apache.kafka.streams.processor.WallclockTimestampExtractor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration

@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaStreamsConfig {

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamConfig(): KafkaStreamsConfiguration {
        return KafkaStreamsConfiguration(
            mapOf(
                APPLICATION_ID_CONFIG to "test",
                BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
                DEFAULT_KEY_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
                DEFAULT_VALUE_SERDE_CLASS_CONFIG to Serdes.String().javaClass.name,
                DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG to WallclockTimestampExtractor().javaClass.name
            )
        )
    }
}