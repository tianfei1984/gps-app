package cn.com.gps169.common.kafka;

import java.util.HashMap;
import java.util.Map;

public class KafkaFactory {
    private static KafkaProducer kafkaProducer;

    private static Map<String, KafkaConsumer> consumerPool = new HashMap<String,KafkaConsumer>();
    
    public static KafkaProducer getProducer() {
        if (kafkaProducer == null) {
            kafkaProducer = new KafkaProducer();
        }

        if (!kafkaProducer.isOpened()) {
            kafkaProducer.open();
        }

        return kafkaProducer;
    }

    public static KafkaConsumer getConsumer(String topic, MessageHandler handler) {
        KafkaConsumer kafkaConsumer = consumerPool.get(topic);
        if (kafkaConsumer == null) {
            kafkaConsumer = new KafkaConsumer(topic, handler);
            consumerPool.put(topic, kafkaConsumer);
        }

        if (!kafkaConsumer.isOpened()) {
            kafkaConsumer.open();
        }

        return kafkaConsumer;
    }
    
    @Override
    protected void finalize() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }

        for(String topic:consumerPool.keySet()){
            consumerPool.get(topic).close();
        }
    }
}
