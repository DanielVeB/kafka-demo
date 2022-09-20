package com.kurosz.daniel.demo.processorapi

import kafka.example.Cat
import org.apache.kafka.streams.processor.api.Processor
import org.apache.kafka.streams.processor.api.ProcessorContext
import org.apache.kafka.streams.processor.api.ProcessorSupplier
import org.apache.kafka.streams.processor.api.Record
import org.slf4j.LoggerFactory

class CatProcessor : ProcessorSupplier<String, Cat, Int, String> {

    companion object {
        private val logger = LoggerFactory.getLogger(CatProcessor::class.java)
    }

    override fun get(): Processor<String, Cat, Int, String> {

        return object : Processor<String, Cat, Int, String> {

            private lateinit var context: ProcessorContext<Int, String>
            override fun init(context: ProcessorContext<Int, String>) {
                this.context = context
            }

            override fun process(record: Record<String, Cat>) {
                logger.info("Process record (key: ${record.key()}, value: ${record.value()}")
                context.forward(Record(record.key().length, record.value().name, 1L))
            }

        }
    }
}