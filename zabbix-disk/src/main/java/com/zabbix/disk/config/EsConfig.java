package com.zabbix.disk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>标题: elasticsearch 配置文件信息</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2018</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: zhx
 * @date: 2018/03/15
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class EsConfig {

    public static String PORT;

    public static String HOST;

    public static String SCHEMA = "http";

    public static String LOGREQINDEX = "log-req-*";

    public static String LOGSQLINDEX = "log-sql-*";

    @Value("port")
    public  void setPORT(String port) {
        PORT = port;
    }

    @Value("host")
    public  void setHost(String host) {
        HOST = host;
    }

}
