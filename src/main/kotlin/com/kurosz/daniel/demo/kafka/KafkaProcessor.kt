package com.kurosz.daniel.demo.kafka

import com.kurosz.daniel.demo.config.KafkaStreamsConfig
import com.kurosz.daniel.demo.processorapi.CatProcessor
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import kafka.example.Cat
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class KafkaProcessor(@Autowired val kafkaStreamsConfig: KafkaStreamsConfig) {
    companion object {
        private val stringSerde = Serdes.String()
        private val intSerde = Serdes.Integer()
        private val catAvro = SpecificAvroSerde<Cat>()
        private val logger = LoggerFactory.getLogger(KafkaProcessor::class.java)
    }

    @Autowired
    fun buildTopology(streamBuilder: StreamsBuilder) {
        val serdeConfig = mapOf(
            AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "mock://localhost:1234"
        )
        val catSerde = catAvro.apply { configure(serdeConfig, true) }

        val topology = streamBuilder.build()

        topology.addSource(
            "cat-source",
            stringSerde.deserializer(),
            catSerde.deserializer(),
            kafkaStreamsConfig.inputTopic
        )
        topology.addProcessor("cat-processor", CatProcessor(), "cat-source")
        topology.addSink(
            "cat-output",
            kafkaStreamsConfig.outputTopic,
            intSerde.serializer(),
            stringSerde.serializer(),
            "cat-processor"
        )
    }
}