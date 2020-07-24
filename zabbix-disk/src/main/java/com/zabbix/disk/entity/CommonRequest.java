package com.zabbix.disk.entity;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class CommonRequest {

    private String jsonrpc = "2.0";

    private Object params;

    private String method;

    private String auth;

    private String id = UUID.randomUUID().toString();

}
