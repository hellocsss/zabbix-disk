package com.zabbix.disk.service;

import com.zabbix.disk.entity.ChartBase;
import com.zabbix.disk.entity.page.Item;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Component
public class ZabbixServiceItem {

    public List<String> splictString(Map<String, Item> itemMap){
           List<String> list=new ArrayList<>();
        itemMap.forEach((key, value) -> {
            String substring = key.substring(0, key.indexOf(":"));
            list.add(substring);
        });
        List<String> collect = list.stream().distinct().collect(Collectors.toList());
        return  collect;
    }

    public  String getPrintSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
