package com.zabbix.disk.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.aspectj.weaver.ast.Var;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

public class text {

    private static  void sbAdd(StringBuilder sb, String title, String content) {
        sb.append(title);
        sb.append(":  ");
        sb.append(content);
        sb.append("\n");
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("account", ""));
        formparams.add(new BasicNameValuePair("password", ""));
        HttpEntity reqEntity = new UrlEncodedFormEntity(formparams, "utf-8");




        boolean requestId = StringUtils.isBlank("requestId");


       String type="abc";
       String id="2";
        StringBuilder content = new StringBuilder();
        sbAdd(content, "重复", "String.valueOf(warnLog.getTimes())");

        sbAdd(content, "医院", "warnLog.getHospitalName()");
        sbAdd(content, "错误", "warnLog.getErrorContent()");

        System.out.println(content.toString());
        String format2 = String.format("http://his5.zoesoft.com.cn/monitor/wechat/%s/%s", type, id);
        String format3 = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=wxf3bd7a2c9be9f231&redirect_uri=%s&response_type=code&" +
                "scope=snsapi_base&state=1#wechat_redirect", format2);
        String hostIp = "http://192.168.4.21:8081";
        String appID = "wxf3bd7a2c9be9f231";
        String tmpID = "hDGxF-iwJwuW5t5Jb404PvZc3qAEG6uv6Cdq8RIdpSY";
        String apiUrl = String.format("%s/wx/msg/%s/sendTemplateMsg/%s", hostIp, appID, tmpID);

        /*测试map 创建的是一个hashmap*/
        System.out.println("huttol");
        TimeInterval timer3 = DateUtil.timer();
        Map<String, String> map = MapUtil.newHashMap();
        System.out.println(timer3.interval());
        System.out.println("常规");
        TimeInterval timer4 = DateUtil.timer();
        Map<String,String> map1=new HashMap<>();
        System.out.println(timer4.interval());


        //测试时间
        TimeInterval timer = DateUtil.timer();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-HH-dd HH:mm:ss");
        String format = simpleDateFormat.format(new Date());
        System.out.println(timer.interval());

        TimeInterval timer2 =  DateUtil.timer();
      /*  DateTime dateTime=new DateTime();*/
        String format1 = DateUtil.format(new Date(), "yyyy-HH-dd HH:mm:ss");
        System.out.println(timer2.interval());

        System.out.println(format+"SimpleDateFormat");
        System.out.println(format1+"hutool");

    }



    public static void main1(String[] args) {
        //创建缓存，默认4毫秒过期
        TimedCache<String, String> timedCache = CacheUtil.newTimedCache(5);
        timedCache.put("key1", "value1", DateUnit.SECOND.getMillis() * 200);
        timedCache.put("key2", "value2", DateUnit.SECOND.getMillis() * 200);
        timedCache.put("key3", "value3");
        timedCache.schedulePrune(5);
        Iterator<CacheObj<String, String>> cacheObjIterator = timedCache.cacheObjIterator();
        while (cacheObjIterator.hasNext()){
            CacheObj<String, String> next = cacheObjIterator.next();
            System.out.println(next.getKey()+"值");
        }
//等待5毫秒
        ThreadUtil.sleep(5);
//5毫秒后由于value2设置了5毫秒过期，因此只有value2被保留下来
        String value1 = timedCache.get("key1");
        String value2 = timedCache.get("key2");

//5毫秒后，由于设置了默认过期，key3只被保留4毫秒，因此为null
        String value3 = timedCache.get("key3");

        Iterator<CacheObj<String, String>> cacheObjIterator2 = timedCache.cacheObjIterator();
        while (cacheObjIterator2.hasNext()){
            CacheObj<String, String> next = cacheObjIterator2.next();
            System.out.println(next.getKey()+"值");
        }
        String value5 = timedCache.get("key1");
        String value4 = timedCache.get("key2");
//取消定时清理
    /*   timedCache.cancelPruneSchedule();*/
        timedCache.put("key3", "value4",DateUnit.SECOND.getMillis() * 200);

        Iterator<CacheObj<String, String>> cacheObjIterator3 = timedCache.cacheObjIterator();
        while (cacheObjIterator3.hasNext()){
            CacheObj<String, String> next = cacheObjIterator3.next();
            System.out.println(next.getKey()+"值");
        }
        String value6 = timedCache.get("key1");
        String value7 = timedCache.get("key2");
        String value8 = timedCache.get("key3");
    }


}
