package cn.com.gps169.common.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.gps169.common.tool.ConfigUtil;
import cn.com.gps169.common.tool.Tools;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumer {
    private final static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private ConsumerConnector consumer;
    private String topic;
    private MessageHandler handler;

    public KafkaConsumer(String topic, MessageHandler handler) {
        this.topic = topic;
        this.handler = handler;
    }

    public void open() {
        Properties props = new Properties();
        try {
            InputStream inputStream = ConfigUtil.getConfigReader().getResourceAsStream("/kafka.properties");
            props.load(inputStream);
            //判断一下，那些topic是需要广播接收
            String broadcast_topics = props.getProperty("broadcast.topics");
            if(null!=broadcast_topics&&!broadcast_topics.equals("")){
                if(broadcast_topics.indexOf(topic)>=0){
                    String groupId = props.getProperty("group.id");
                    props.setProperty("group.id", groupId+"_"+Tools.getHostAddress());
                }
            }
            
            
            consumer = kafka.consumer.Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
            FetchMessageThread thread = new FetchMessageThread(topic, consumer, handler);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(String.format("读取kafka配置文件失败。 ErrorMsg:[%s]", e.getMessage()), e);
        }
    }

    public void close() {
        if (consumer != null) {
            consumer.shutdown();
            consumer = null;
        }
    }

    public boolean isOpened() {
        return consumer != null;
    }

    public ConsumerConnector getConsumerConnector() {
        return consumer;
    }

    
    protected static class FetchMessageThread extends Thread {
        private ConsumerConnector consumer;
        private String topic;
        private MessageHandler handler;

        public FetchMessageThread(String topic, ConsumerConnector consumer, MessageHandler handler) {
            this.consumer = consumer;
            this.topic = topic;
            this.handler = handler;
        }

        @Override
        public void run() {
            if (handler != null) {
                Map<String, Integer> topicMap = new HashMap<String, Integer>();
                topicMap.put(topic, new Integer(1));
                Map<String, List<KafkaStream<byte[], byte[]>>> streamMap = consumer.createMessageStreams(topicMap);
                KafkaStream<byte[], byte[]> stream = streamMap.get(topic).get(0);
                ConsumerIterator<byte[], byte[]> it = stream.iterator();
                while (it.hasNext()) {
                    handler.handle(new String(it.next().message()));
                }
            }
        }
    }
}
