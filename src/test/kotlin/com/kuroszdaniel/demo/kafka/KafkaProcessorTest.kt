package com.kuroszdaniel.demo.kafka

import org.apache.kafka.common.serialization.Serdes
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
    private lateinit var inputTopic: TestInputTopic<String, String>
    private lateinit var outputTopic: TestOutputTopic<String, String>

    @BeforeAll
    fun setUp() {
        kafkaProcessor = KafkaProcessor()
    }

    @BeforeEach
    fun beforeEach() {
        val streamBuilder = StreamsBuilder()
        kafkaProcessor.buildPipeline(streamBuilder)
        val topology = streamBuilder.build()

        // Dummy properties needed for test diver
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

        topologyTestDriver = TopologyTestDriver(topology, props)

        inputTopic = topologyTestDriver.createInputTopic("testTopic", StringSerializer(), StringSerializer())
        outputTopic = topologyTestDriver.createOutputTopic("testTopic2", StringDeserializer(), StringDeserializer())

    }


    @Test
    fun `should put to output topic upper cased message`() {
        assertTrue(outputTopic.isEmpty)
        inputTopic.pipeInput("key", "value")

        assertEquals(outputTopic.readKeyValue(), KeyValue("key", "VALUE"))
    }

}