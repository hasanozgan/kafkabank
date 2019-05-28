package com.hasanozgan.kafkabank.transactionproducer.service

import com.hasanozgan.kafkabank.models.Transaction
import com.hasanozgan.kafkabank.models.TransactionType
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.joda.time.DateTime
import kotlin.random.Random

class TransactionGenerator(val accountSize: Int, val transactionLimitFrom: Int, val transactionLimitUntil: Int, val openingBalance: Double) {
    fun generate(): Channel<Transaction> {
        val channel = Channel<Transaction>()

        GlobalScope.launch {
            (1 until accountSize)
                    .flatMap {
                        generateAccountWithTransactions(Random.nextInt(transactionLimitFrom, transactionLimitUntil))
                    }
                    .forEach { transaction ->
                        channel.send(transaction)
                    }

            channel.close() // we're done sending
        }

        return channel
    }

    private fun isFraud(): Boolean {
        return (Random.nextInt(100) % 5 == 0)
    }

    private fun deposit(customerID: String): Transaction {
        return Transaction(TransactionType.DEPOSIT,
                customerID,
                Random.nextInt(1, 100).toDouble(),
                isFraud(),
                DateTime())
    }

    private fun withdraw(customerID: String): Transaction {
        return Transaction(TransactionType.WITHDRAW,
                customerID,
                Random.nextInt(1, 100).toDouble(),
                isFraud(),
                DateTime())
    }

    private fun generateAccountWithTransactions(transactionLimit: Int): Set<Transaction> {
        val customerID = String.format("%s-%s:%s", Faker.name.firstName(), Faker.name.lastName(), Faker.idNumber.invalid()).toUpperCase()

        return listOf(Transaction(
                TransactionType.OPENING,
                customerID,
                openingBalance,
                false,
                DateTime())
        ).union((1 until transactionLimit).map {
            if (Random.nextInt() % 2 == 0) {
                deposit(customerID)
            } else {
                withdraw(customerID)
            }
        })
    }
}