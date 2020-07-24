package com.zabbix.disk.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.zabbix.disk.config.ClientRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class AccessRequest {

    //开发者提供
    private static String appid="appid";

    //开发者提供
    private static String secret ="secret";
   /* private  static  String url="https://api.weixin.qq.com/cgi-bin/token?" +
            "grant_type=client_credential&appid=APPID&secret=APPSECRET";*/

    final String WC_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxf3bd7a2c9be9f231&secret=3b3aa12a929e7528a8e7ab88a7dd605c&code=%s&grant_type=authorization_code";
    @Cached(name = "accessToken",cacheType= CacheType.LOCAL)
    public  String AccessToken() throws IOException {
        System.out.println("进来了啊");
        String accessToken = ClientRequest.sendGet(WC_ACCESS_TOKEN);
        System.out.println("我执行了吗"+ new DateTime());

        return accessToken;
    }
}
