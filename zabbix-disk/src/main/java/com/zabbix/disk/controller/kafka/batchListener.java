package com.zabbix.disk.controller.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class batchListener {
    private static final Logger log= LoggerFactory.getLogger(batchListener.class);

    /*拉取多个消息*/
    private Map<String,Object> mapconfig2(){
        Map<String,Object> map=new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.0.5.211:9092");
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        map.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        map.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        //一次拉取消息数量
        map.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return map;
    }
    /*ack 拉取消息*/
    private Map<String,Object> mapconfig(){
        Map<String,Object> map=new HashMap<>();
        map.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.0.5.211:9092");
        map.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        map.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        map.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        map.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "3");
        map.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"latest");
        map.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        map.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return map;
    }
    private Map<String, Object> senderProps (){
        Map<String, Object> props = new HashMap<>();
        //连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.5.134:9092");
        //重试，0为不启用重试机制
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        //控制批处理大小，单位为字节
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        //批量发送，延迟为1毫秒，启用该功能能有效减少生产者发送消息次数，从而提高并发量
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        //生产者可以使用的总内存字节来缓冲等待发送到服务器的记录
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024000);
        //键的序列化方式   IntegerSerializer   看情况而定
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //值的序列化方式
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }


    @Bean("batchContainerFactory")
    // ConcurrentKafkaListenerContainerFactory 容器
    public ConcurrentKafkaListenerContainerFactory listenerContainer() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(mapconfig()));
        /*过滤器3*/
        container.setRecordFilterStrategy(new RecordFilterStrategy() {
            @Override
            public boolean filter(ConsumerRecord consumerRecord) {

                return false;
            }
        });


       /*转发*/
        container.setReplyTemplate(new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(senderProps())));
        container.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(3);
        //设置为批量监听
        container.setBatchListener(true);
        return container;
    }


    @Bean("batchContainerFactory2")
    // ConcurrentKafkaListenerContainerFactory 容器
    public ConcurrentKafkaListenerContainerFactory listenerContainer2() {
        ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
        container.setConsumerFactory(new DefaultKafkaConsumerFactory(mapconfig2()));

        //设置并发量，小于或等于Topic的分区数
        container.setConcurrency(3);
        //设置为批量监听
        container.setBatchListener(true);
        return container;
    }

    //采用ack方式
    @SendTo("clustergroup")
  @KafkaListener(topics = {"batchtopic"},id="batch",containerFactory = "batchContainerFactory")
    public String listlisteren(List<ConsumerRecord<?,?>> date, Acknowledgment acknowledgment){
      acknowledgment.acknowledge();
        System.out.println("batch进来了吗");


      for (ConsumerRecord s:
      date) {

          //System.out.println(s+"这时候采用ackConsumerRecord");

          return s.value()+"nihao";

      }
      log.info("topic.quick.batch  receive : "+date.toString());
        /*for (ConsumerRecord<Object,Object> v:recordList){
            System.out.println("batch简单消费："+v.topic()+"-"+v.partition()+"-"+v.value());
        }*/
      return "nihao";
    }

    //不采用ack方式
    @KafkaListener(topics = {"batchtopic"},id="batch3",containerFactory = "batchContainerFactory2")
    public void listlisteren2(List<ConsumerRecord<?,?>> date){
        System.out.println("batch不采用ack");
        for (ConsumerRecord s:
                date) {
            System.out.println(s+"这时候不采用ConsumerRecord");
        }
        log.info("topic.quick.batch  receive : "+date.toString());
        /*for (ConsumerRecord<Object,Object> v:recordList){
            System.out.println("batch简单消费："+v.topic()+"-"+v.partition()+"-"+v.value());
        }*/

    }

    @KafkaListener(topics = {"clustergroup"},id="clustergroup2",containerFactory = "batchContainerFactory2")
    public void clustergroup(List<ConsumerRecord<?,?>> date){
        System.out.println("clustergroup  134进行接收");
        for (ConsumerRecord s:
                date) {
            System.out.println(s+"134进行接收能收到打印的东西吗");
        }
        log.info("topic.quick.batch  receive : "+date.toString());
        /*for (ConsumerRecord<Object,Object> v:recordList){
            System.out.println("batch简单消费："+v.topic()+"-"+v.partition()+"-"+v.value());
        }*/

    }


}
