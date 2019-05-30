# kafkabank
kafkabank a poc

 - confluent stack
 - kafka broker
 - kafka connnector
 - kafka streams

```sh
confluent start
```



__*create topics*__
```sh
kafka-topics --create --topic transactions --partitions 5 --replication-factor 1 --zookeeper localhost:2181
kafka-topics --create --topic account-balance --partitions 5 --replication-factor 1 --zookeeper localhost:2181
```

__*postgresql*__
```sh
docker run -d -p 5432:5432 --name my-postgres -e POSTGRES_PASSWORD=postgres postgres:9.6
```

__*register connectors*__
```sh
confluent load SinkTopics -d kafka-connectors/SinkTopicsInDb.properties
```

### transaction-producer

```sh
./gradlew clean shadowJar
java -jar ./build/libs/transaction-producer-all.jar
```


### balance aggregator `with kafka-streams` 

```sh
./gradlew clean shadowJar
java -jar ./build/libs/balance-aggregator-all.jar
```

```sh
kafka-avro-console-consumer --bootstrap-server localhost:9092 --topic account-balance --from-beginning
``` 
 
 
```sh
confluent stop
```
