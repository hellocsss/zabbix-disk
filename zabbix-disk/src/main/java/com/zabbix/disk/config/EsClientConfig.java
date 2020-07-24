package com.zabbix.disk.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
//@ConfigurationProperties(prefix = "elasticsearch")
public class EsClientConfig {

    public static String HOST;

    public static String PORT;

    public static String SCHEME;

    @Value("host")
    public void setHOST(String host) {
        HOST = host;
    }

    @Value("port")
    public void setPORT(String port) {
        PORT = port;
    }
    @Value("scheme")
    public void setSCHEME(String scheme) {
        SCHEME = scheme;
    }
}

