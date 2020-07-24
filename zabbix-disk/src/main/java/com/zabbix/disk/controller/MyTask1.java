package com.zabbix.disk.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.cron.CronUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class MyTask1 {

    public static int count = 0;
    public void start(){
        System.out.printf("第%d次执行定时任务，当前时间：%s\n",++count, DateTime.now().toString());
    }

    public static void main(String[] args) throws InterruptedException {
      /*  CronUtil.setMatchSecond(true);
        CronUtil.start();
        //可以手动停止定时任务
        //CronUtil.stop();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();*/


        List<User> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setName("wode");
            user.setValue("nihao");
            list.add(user);
        }

        /*hutool*/
        TimeInterval timer4 = DateUtil.timer();
        String parse = JSONUtil.toJsonStr(list);
        /* System.out.println(parse+"没有删除的json对象2");*/
        System.out.println(timer4.interval());
        /*fastjson*/
        TimeInterval timer3 = DateUtil.timer();
        String strmap = JSONObject.toJSONString(list);
        System.out.println(timer3.interval());
        /*     System.out.println(strmap+"没有删除的json对象");*/

        System.out.println("以下测试转json字符串转对象");

        String parse2= JSONUtil.toJsonStr(list);


        TimeInterval timer6 = DateUtil.timer();
        List<User> o = JSONUtil.toBean(parse2, List.class, false);

        System.out.println(timer6.interval());


        TimeInterval timer5 = DateUtil.timer();
        List list1 = JSONObject.parseObject(strmap, List.class);
        System.out.println(timer3.interval());

    }
}
