package com.kuroszdaniel.demo.kafka

import com.kuroszdaniel.demo.config.KafkaStreamsConfig
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
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
internal class KafkaProcessorIT {

    @Autowired
    lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    @Test
    fun `Consumer should read value from topic`() {
        val producer = configureProducer()
        val consumer = configureConsumer()

        producer.send(ProducerRecord("messages", 1, "value"))

        val result = consumer.poll(Duration.ofMillis(5000)).first()

        consumer.close()

        assertEquals("VALUE", result.value())

    }

    private fun configureConsumer(): Consumer<Int, String> {
        val consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker)
        consumerProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        val consumer: Consumer<Int, String> = DefaultKafkaConsumerFactory<Int, String>(consumerProps)
            .createConsumer()
        consumer.subscribe(Collections.singleton("upper_messages"))
        return consumer
    }

    private fun configureProducer(): Producer<Int, String> {
        val producerProps: Map<String, Any> = HashMap(KafkaTestUtils.producerProps(embeddedKafkaBroker))
        return DefaultKafkaProducerFactory<Int, String>(producerProps).createProducer()
    }
}