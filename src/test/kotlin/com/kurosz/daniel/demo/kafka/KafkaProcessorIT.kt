package com.kurosz.daniel.demo.kafka

import com.kurosz.daniel.demo.config.KafkaStreamsConfig
import com.kurosz.daniel.demo.kafka.utils.KafkaConfig
import kafka.example.Breed
import kafka.example.Cat
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import java.time.Duration
import java.util.*


@SpringBootTest(classes = [KafkaProcessor::class, KafkaStreamsConfig::class])
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = [
        "listeners=PLAINTEXT://localhost:9091",
        "port=9091"
    ],
    topics = ["messages", "upper_messages"]
)
@Import(value = [KafkaConfig::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class KafkaProcessorIT {

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Autowired
    lateinit var kafkaConfig: KafkaConfig

    private lateinit var producer: Producer<String, Cat>

    private lateinit var consumer: Consumer<Int, String>

    @BeforeAll
    fun setup() {
        producer = kafkaConfig.configureCatProducer(embeddedKafkaBroker)
        consumer = kafkaConfig.configureConsumer(embeddedKafkaBroker)

        consumer.subscribe(Collections.singleton("upper_messages"))
    }

    @Test
    fun `Consumer should read value from topic`() {
        producer.send(ProducerRecord("messages", "key", Cat(Breed.BIRMAN, "kitty")))

        val result = consumer.poll(Duration.ofMillis(5000)).first()

        consumer.close()
        assertEquals("kitty", result.value())
    }

}