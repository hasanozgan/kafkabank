package com.hasanozgan.kafkabank.balanceaggregator.kafkaclient

import com.hasanozgan.kafkabank.balanceaggregator.service.TransactionTimestampExtractor
import com.hasanozgan.kafkabank.models.AccountBalance
import com.hasanozgan.kafkabank.models.Transaction
import com.hasanozgan.kafkabank.models.TransactionType
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.utils.Bytes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Predicate
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.KeyValueStore
import org.slf4j.LoggerFactory
import java.util.*
import org.apache.kafka.streams.kstream.Materialized.`as` as materializedAs


class KafkaConsumerClient(val applicationId: String, val bootstrapServers: String, val schemaRegistryUrl: String, val transactionTopicName: String, val balanceTopicName: String) {
    private val logger = LoggerFactory.getLogger(KafkaConsumerClient::class.java.getSimpleName())

    val props = Properties();

    init {
        props[StreamsConfig.APPLICATION_ID_CONFIG] = applicationId;
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers;
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest";
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass;
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = SpecificAvroSerde::class.java;
        props[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = schemaRegistryUrl;
    }

    fun createTopology(): KafkaStreams {
        // define a few serdes that will be useful to us later
        val transactionSpecificAvroSerde = SpecificAvroSerde<Transaction>()
        val accountBalanceSpecificAvroSerde = SpecificAvroSerde<AccountBalance>()

        transactionSpecificAvroSerde.configure(Collections.singletonMap(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl), false)
        accountBalanceSpecificAvroSerde.configure(Collections.singletonMap(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl), false)

        val longSerde = Serdes.LongSerde()
        val stringSerde = Serdes.StringSerde()

        val builder = StreamsBuilder()

        // we build our stream with a timestamp extractor
        val streams = builder
                .stream(transactionTopicName, Consumed.with(longSerde,
                        transactionSpecificAvroSerde,
                        TransactionTimestampExtractor(),
                        null)
                ).selectKey({  key, value -> value.customerId })


        val branches = streams.branch(
                Predicate{ key:String, value:Transaction -> value.isFraud == true }, /* first predicate */
                Predicate{ key:String, value:Transaction -> value.isFraud == false } /* first predicate */
        )


        branches[1]
                .peek { customerID, transaction -> println(transaction) }
                .groupByKey()
                .aggregate(
                        { emptyAccountBalance() },
                        { customerId: String, transaction: Transaction, accountBalance: AccountBalance -> this.transactionAggregator(customerId, transaction, accountBalance) },
                        Materialized.`as`<String, AccountBalance, KeyValueStore<Bytes, ByteArray>>("account-balance-table").withValueSerde(accountBalanceSpecificAvroSerde)

                )
                .toStream()
                .to(balanceTopicName, Produced.with(stringSerde, accountBalanceSpecificAvroSerde));


        return KafkaStreams(builder.build(), props)
    }

    private fun emptyAccountBalance() = AccountBalance.newBuilder().setCustomerId("N/A").setAmount(0.0).build()

    private fun transactionAggregator(key: String, newTransaction: Transaction, currentBalance: AccountBalance): AccountBalance {
        val accountBalanceBuilder: AccountBalance.Builder = AccountBalance.newBuilder(currentBalance)

        var newBalance = 0.0
        if (newTransaction.transactionType == TransactionType.OPENING) {
            newBalance = accountBalanceBuilder.getAmount() + newTransaction.getAmount()
        } else if (newTransaction.transactionType == TransactionType.DEPOSIT) {
            newBalance = accountBalanceBuilder.getAmount() + newTransaction.getAmount()
        } else if (newTransaction.transactionType == TransactionType.WITHDRAW) {
            newBalance = accountBalanceBuilder.getAmount() - newTransaction.getAmount()
        }

        accountBalanceBuilder.setCustomerId(newTransaction.customerId)
        accountBalanceBuilder.setAmount(newBalance)

        println(newTransaction)
        println(newBalance)


        return accountBalanceBuilder.build();
    }
}