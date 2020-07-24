package com.zabbix.disk.controller.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;


public class recordFilterStrategyFile implements RecordFilterStrategy {
    @Override
    public boolean filter(ConsumerRecord consumerRecord) {


        return false;
    }
}
