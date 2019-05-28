package com.hasanozgan.kafkabank.balanceaggregator.service

import com.hasanozgan.kafkabank.models.Transaction
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor

class TransactionTimestampExtractor : TimestampExtractor {

    override fun extract(record: ConsumerRecord<Any, Any>, previousTimestamp: Long): Long {
        var timestamp: Long

        val transaction = record.value() as Transaction
        timestamp = transaction.transactionTime.getMillis()
        return if (timestamp < 0) {
            // Invalid timestamp!  Attempt to estimate a new timestamp,
            // otherwise fall back to wall-clock time (processing-time).
            if (previousTimestamp >= 0) {
                previousTimestamp
            } else {
                System.currentTimeMillis()
            }
        } else {
            timestamp
        }
    }
}
