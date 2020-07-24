package com.zabbix.disk;

import cn.hutool.cron.CronUtil;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 *
 */
/**/
@SpringBootApplication
@EnableMethodCache(basePackages = "com.zabbix.disk.dao")
@EnableCreateCacheAnnotation
public class ZabbixDevApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZabbixDevApplication.class, args);
        /*支持级别定时*/
      /*  CronUtil.setMatchSecond(true);
        CronUtil.start(true);*/

    }
}
