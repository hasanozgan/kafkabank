package com.hasanozgan.kafkabank.transactionproducer.service

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import org.slf4j.LoggerFactory

class TransactionProducer(val transactionGenerator: TransactionGenerator, val transactionSender: TransactionSender) {
    @ObsoleteCoroutinesApi
    suspend fun produce() {
        val transactionChan = transactionGenerator.generate()
        transactionSender.send(transactionChan)
    }

}