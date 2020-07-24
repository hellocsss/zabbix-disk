package com.zabbix.disk.entity;

import lombok.Data;

import java.util.List;

@Data
public class DiskLine {
    private List<String> xAxis;
    private List<String> series;
}
