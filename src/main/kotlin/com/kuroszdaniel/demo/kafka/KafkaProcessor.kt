package com.kuroszdaniel.demo.kafka

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class KafkaProcessor() {
    companion object {
        val STRING_SERDE: Serde<String> = Serdes.String()
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        streamsBuilder
            .stream("testTopic", Consumed.with(STRING_SERDE, STRING_SERDE))
            .map { key, value ->
                println("HELLO: $key + $value")
                KeyValue(key, value.uppercase())
            }
            .to("testTopic2")
    }

}