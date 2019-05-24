package com.hasanozgan.kafkabank.transactionproducer.service

import com.hasanozgan.kafkabank.models.Transaction
import com.hasanozgan.kafkabank.transactionproducer.kafkaclient.KafkaProducerClient
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import org.slf4j.LoggerFactory

class TransactionSender(val kafkaProducerClient: KafkaProducerClient) {
    private val logger = LoggerFactory.getLogger(TransactionSender::class.java.getSimpleName())

    @ObsoleteCoroutinesApi
    suspend fun send(transactionChan: Channel<Transaction>) {
        transactionChan.consumeEach { transaction ->
            logger.info(transaction.toString())
            kafkaProducerClient.send(transaction)
        }
    }
}