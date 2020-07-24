package com.zabbix.disk.controller.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;


import org.apache.kafka.common.internals.Topic;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import javax.xml.transform.Source;



public class KafkaConsumer {

    /*https://www.jianshu.com/p/a64defb44a23 参数讲解 @KafkaListener */
   @KafkaListener(containerGroup="defaultConsumerGrouptest8",topicPartitions = {
           @TopicPartition(topic = "batchtopic",partitions = {"0","1"}),

   })
    public void onMessage1(ConsumerRecord<?, ?> record){
       System.out.println("我是分区 0  1");
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("我是分区 0  1："+record.topic()+"-"+record.partition()+"-"+record.value());
    }
    /*——————————————————————一个组里面的多个消费者 消费不同的分区————————————————————*/
    @KafkaListener(containerGroup="defaultConsumerGrouptest8",topicPartitions = {
            @TopicPartition(topic = "batchtopic",partitions = {"2","3"})
    })
    public void onMessages(ConsumerRecord<?, ?> record){
        System.out.println("进来了吗");
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("我是分区 2 3："+record.topic()+"-"+record.partition()+"-"+record.value());
    }



    @SendTo("batchtopic")
    @KafkaListener(topics = {"test5"},id="default",groupId = "SendTo")
    public String onMessage2(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        //System.out.println("oneTopic2简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());

      return (String) record.value();
    }

    @KafkaListener(topics = {"oneTopic2"},id="default2",groupId = "defaultConsumerGroup2")
    public void oneTopic2(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("oneTopic2简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }

/*
   @KafkaListener(id="batch",topicPartitions ={
           @TopicPartition(topic = "batchtopic",partitions = {"0","1"}),
           @TopicPartition(topic = "batchtopic",partitions = {"2","3"})
   })
    public void listlisteren(ConsumerRecord<?, ?> record) {
        System.out.println("batch进来了吗");
        System.out.println("简单消费：" + record.topic() + "-" + record.partition() + "-" + record.value());
    }
*/

   // @KafkaListener(topics = {"batchtopic"},id="batch")
    public void listlisteren(ConsumerRecord<?, ?> record) {
        System.out.println("batch进来了吗");
        //System.out.println("batchtopic简单消费：" + record.topic() + "-" + record.partition() + "-" + record.value());
    }


}
