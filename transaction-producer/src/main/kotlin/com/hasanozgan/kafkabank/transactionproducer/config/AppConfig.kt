package com.hasanozgan.kafkabank.transactionproducer.config


import com.typesafe.config.ConfigFactory

class AppConfig() {
    val bootstrapServers: String
    val schemaRegistryUrl: String
    val topicName: String
    val openingBalance: Double
    val accountSize: Int
    val transactionLimitFrom: Int
    val transactionLimitUntil: Int


    init {
        val config = ConfigFactory.load()
        this.bootstrapServers = config.getString("kafka.bootstrap.servers")
        this.schemaRegistryUrl = config.getString("kafka.schema.registry.url")
        this.topicName = config.getString("kafka.topic.name")

        this.openingBalance = config.getDouble("app.transactions.data.openingBalance")
        this.accountSize = config.getInt("app.transactions.data.accountSize")
        this.transactionLimitFrom = config.getInt("app.transactions.data.transactionLimitFrom")
        this.transactionLimitUntil = config.getInt("app.transactions.data.transactionLimitUntil")
    }
}
