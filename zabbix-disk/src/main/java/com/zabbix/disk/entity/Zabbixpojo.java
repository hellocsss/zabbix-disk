package com.zabbix.disk.entity;

import com.zabbix.disk.entity.page.Item;
import lombok.Data;

import java.util.List;

@Data
public class Zabbixpojo {
    private String host;
    private String hostid;
    private List<Item> items;
}
