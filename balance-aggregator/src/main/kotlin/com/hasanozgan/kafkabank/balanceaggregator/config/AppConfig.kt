package com.hasanozgan.kafkabank.balanceaggregator.config


import com.typesafe.config.ConfigFactory

class AppConfig() {
    val applicationID: String
    val bootstrapServers: String
    val schemaRegistryUrl: String
    val transactionTopicName: String
    val balanceTopicName: String

    init {
        val config = ConfigFactory.load()
        this.applicationID = config.getString("kafka.streams.application.id")
        this.bootstrapServers = config.getString("kafka.bootstrap.servers")
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url")
        this.transactionTopicName = config.getString("kafka.topic.transactions.name")
        this.balanceTopicName = config.getString("kafka.topic.balance.name")
    }
}
