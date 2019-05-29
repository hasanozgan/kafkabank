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
            1.rangeTo(accountSize)
                    .flatMap {
                        generateAccountWithTransactions()
                    }
                    .forEach { transaction ->
                        channel.send(transaction)
                    }

            channel.close() // we're done sending
        }

        return channel
    }

    private fun generateAccountWithTransactions(): List<Transaction> {
        val customerID = Faker.idNumber.invalid()
        val transactionFee = 2.5
        val fraudTransaction = Transaction(TransactionType.WITHDRAW, customerID, 99.0, true, DateTime())

        return listOf(
                Transaction(TransactionType.OPENING, customerID, openingBalance, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                fraudTransaction,
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.DEPOSIT, customerID, 10.0, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                fraudTransaction,
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.DEPOSIT, customerID, 5.0, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                Transaction(TransactionType.WITHDRAW, customerID, transactionFee, false, DateTime()),
                fraudTransaction
        )
    }
}