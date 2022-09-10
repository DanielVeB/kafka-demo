package com.kuroszdaniel.demo.kafka

import com.kuroszdaniel.demo.config.KafkaStreamsConfig
import io.mockk.every
import io.mockk.mockkClass
import org.apache.kafka.common.serialization.*
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
    private lateinit var inputTopic: TestInputTopic<Int, String>
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
        kafkaProcessor.buildPipeline(streamBuilder)
        val topology = streamBuilder.build()

        // Dummy properties needed for test diver
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.Integer().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

        topologyTestDriver = TopologyTestDriver(topology, props)

        inputTopic = topologyTestDriver.createInputTopic("testTopic", IntegerSerializer(), StringSerializer())
        outputTopic = topologyTestDriver.createOutputTopic("testTopic2", IntegerDeserializer(), StringDeserializer())

    }


    @Test
    fun `should put to output topic upper cased message`() {
        assertTrue(outputTopic.isEmpty)
        inputTopic.pipeInput(1, "value")

        assertEquals(outputTopic.readKeyValue(), KeyValue(1, "VALUE"))
    }

}