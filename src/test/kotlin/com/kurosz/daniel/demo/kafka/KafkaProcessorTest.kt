package com.kurosz.daniel.demo.kafka

import com.kurosz.daniel.demo.config.KafkaStreamsConfig
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.mockk.every
import io.mockk.mockkClass
import kafka.example.Breed
import kafka.example.Cat
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.kafka.streams.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KafkaProcessorTest {

    private lateinit var kafkaProcessor: KafkaProcessor

    private lateinit var topologyTestDriver: TopologyTestDriver
    private lateinit var inputTopic: TestInputTopic<String, Cat>
    private lateinit var outputTopic: TestOutputTopic<Int, String>

    @BeforeAll
    fun setUp() {
        val config = mockkClass(KafkaStreamsConfig::class)
        every { config.server }.returns("localhost:9092")
        every { config.inputTopic }.returns("testTopic")
        every { config.outputTopic }.returns("testTopic2")
        kafkaProcessor = KafkaProcessor(config)
    }

    @BeforeEach
    fun beforeEach() {
        val streamBuilder = StreamsBuilder()
        kafkaProcessor.buildTopology(streamBuilder)
        val topology = streamBuilder.build()

        val serdeConfig = mapOf(
            AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to "mock://localhost:1234"
        )
        val catAvro = SpecificAvroSerde<Cat>()
        val catSerde = catAvro.apply { configure(serdeConfig, true) }

        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"

        topologyTestDriver = TopologyTestDriver(topology, props)

        inputTopic = topologyTestDriver.createInputTopic("testTopic", StringSerializer(), catSerde.serializer())
        outputTopic = topologyTestDriver.createOutputTopic("testTopic2", IntegerDeserializer(), StringDeserializer())

    }


    @Test
    fun `test kafka topology`() {
        assertTrue(outputTopic.isEmpty)
        inputTopic.pipeInput("key", Cat(Breed.BIRMAN, "kitty"))

        assertEquals(outputTopic.readKeyValue(), KeyValue(3, "kitty"))
    }

}