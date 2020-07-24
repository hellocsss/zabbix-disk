package com.zabbix.disk.controller.kafka;

import com.sun.net.httpserver.Authenticator;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.CompositeProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import sun.java2d.pipe.SpanIterator;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;


@Log4j2
@RestController
public class KafkaProducer {
    @Resource
    private KafkaTemplate<String,Object> kafkaTemplate;


        @GetMapping("/kafka/normal/{message}")
        public void sendMessage(@PathVariable("message") String normalMessage) throws ExecutionException, InterruptedException {
            log.debug("开始生产了吗");
            //同步.get();
           /* kafkaTemplate.send("batchtopic",6,"test", normalMessage).get();*/

            /*addCallback 是一个回调函数*/

            //这种是自定义
            //ProducerFactory defaultKafkaProducerFactory=new DefaultKafkaProducerFactory<>();
            //这种是yml配置
            kafkaTemplate.send("batchtopic",6,"test", normalMessage).addCallback(success ->{
                /*先进行消费在  回调*/
                ProducerRecord<String, Object> producerRecord = success.getProducerRecord();
                String topic = success.getRecordMetadata().topic();
                long offset = success.getRecordMetadata().offset();
                Integer partition = success.getProducerRecord().partition();
                String key = success.getProducerRecord().key();
                System.out.println("partition"+partition+"key"+key+"这里的key是啥");
                System.out.println("topic******"+topic+"-------offset*:*"+offset);

            }, failure ->{
                System.out.println(failure.getMessage());
            });


       /* for (int i = 0; i < 12; i++) {
            kafkaTemplate.send("batchtopic", "test batch listener,dataNum-" + i);
        }*/
    }


}
