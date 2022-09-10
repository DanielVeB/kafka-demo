package com.kuroszdaniel.demo.kafka

import com.kuroszdaniel.demo.config.KafkaStreamsConfig
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class KafkaProcessor(val kafkaStreamsConfig: KafkaStreamsConfig) {
    companion object {
        val STRING_SERDE: Serde<String> = Serdes.String()
        val INT_SERDE: Serde<Int> = Serdes.Integer()
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        streamsBuilder
            .stream(kafkaStreamsConfig.inputTopic, Consumed.with(INT_SERDE, STRING_SERDE))
            .peek{key, value -> println("Key: $key, value: $value") }
            .map { key, value ->
                KeyValue(key, value.uppercase())
            }
            .to(kafkaStreamsConfig.outputTopic)
    }

}