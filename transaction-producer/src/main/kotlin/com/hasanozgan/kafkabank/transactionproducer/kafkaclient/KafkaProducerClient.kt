package com.hasanozgan.kafkabank.transactionproducer.kafkaclient

import com.hasanozgan.kafkabank.models.Transaction
import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import java.util.*


class KafkaProducerClient(val bootstrapServers: String, val schemaRegistryUrl: String, val topicName: String) {

    val kafkaProducer: KafkaProducer<Long, Transaction>

    init {
        val props = Properties();
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers;
        props[ProducerConfig.ACKS_CONFIG] = "all";
        props[ProducerConfig.RETRIES_CONFIG] = Integer.MAX_VALUE;
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 5;
        props[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true;
        props[ProducerConfig.COMPRESSION_TYPE_CONFIG] = "snappy";
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer().javaClass;
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaAvroSerializer().javaClass;
        props[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl;

        this.kafkaProducer = KafkaProducer(props);
    }

    fun send(transaction: Transaction) {
        println(transaction)
        kafkaProducer.send(ProducerRecord<Long, Transaction>(topicName, transaction))
    }

}