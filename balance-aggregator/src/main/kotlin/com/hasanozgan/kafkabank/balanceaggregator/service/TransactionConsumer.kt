package com.hasanozgan.kafkabank.balanceaggregator.service

import com.hasanozgan.kafkabank.balanceaggregator.kafkaclient.KafkaConsumerClient
import org.slf4j.LoggerFactory

class TransactionConsumer(val kafkaConsumerClient: KafkaConsumerClient) {

    fun start() {
        val streams = kafkaConsumerClient.createTopology()
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(Thread(streams::close));
    }
}