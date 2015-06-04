package cn.com.gps169.common.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.common.tool.ConfigUtil;


public class KafkaProducer {
    private final static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private static Integer PARTITION_COUNT = 1000;

    private Producer<String, String> producer;

    public void open() {
        Properties props = new Properties();
        try {
            InputStream inputStream = ConfigUtil.getConfigReader().getResourceAsStream("/kafka.properties");
            props.load(inputStream);

            producer = new Producer<String, String>(new ProducerConfig(props));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(String.format("读取kafka配置文件失败。 ErrorMsg:[%s]", e.getMessage()), e);
        }
    }

    public void close() {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }

    public boolean isOpened() {
        return producer != null;
    }

    public void send(String topic, String message) {
        int p = new Random().nextInt(PARTITION_COUNT);
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, String.valueOf(p), message);
        producer.send(data);
    }

    public void send(String topic, String partition, String message) {
        KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, partition, message);
        producer.send(data);
    }

    public void send(String topic, List<String> msgList) {
        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>();
        int p = new Random().nextInt(PARTITION_COUNT);
        for (String m : msgList) {
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, String.valueOf(p), m);
            messages.add(data);
        }

        producer.send(messages);
    }

    public void send(String topic, String partition, List<String> msgList) {
        List<KeyedMessage<String, String>> messages = new ArrayList<KeyedMessage<String, String>>();
        for (String m : msgList) {
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, partition, m);
            messages.add(data);
        }

        producer.send(messages);
    }
}
