package com.zabbix.disk.entity.weixinmessage;

import lombok.Data;

import java.util.List;

@Data
public class Template {
    private String touser;
    private String template_id;
    private List<TemplateMessage> data;
}
