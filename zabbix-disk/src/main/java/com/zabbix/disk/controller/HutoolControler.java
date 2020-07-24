package com.zabbix.disk.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.CacheObj;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;

import cn.hutool.core.date.*;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.MapProxy;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.pattern.parser.DayOfMonthValueParser;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.RuntimeInfo;
import cn.hutool.system.SystemUtil;
import com.alibaba.fastjson.JSONObject;
import com.zabbix.disk.config.RestTemplateConfig;
import com.zabbix.disk.controller.zabbix.ZabbixControllerItem;
import com.zabbix.disk.entity.ChartBase;
import com.zabbix.disk.entity.Oracle.ExtendsSpaceAndExpirepassword;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HutoolControler {

    @Resource
    private static RestTemplateConfig restTemplateConfig;
 private static     FIFOCache<String, String> fifoCache = CacheUtil.newFIFOCache(3);
    public    static void fol(){

        fifoCache.put("key1", "value1", DateUnit.SECOND.getMillis() * 10);
        fifoCache.put("key2", "value2", DateUnit.SECOND.getMillis() * 600);
        fifoCache.put("key3", "value3", DateUnit.SECOND.getMillis() * 360);

        Iterator<CacheObj<String, String>> cacheObjIterator = fifoCache.cacheObjIterator();
        while (cacheObjIterator.hasNext()){
            CacheObj<String, String> next = cacheObjIterator.next();
            String key = next.getKey();
            String value = next.getValue();
        /*    if ( next.getKey()==null){
                //如果当前值为null 就表示当前缓存失效 就进行移除
                fifoCache.remove(next.getKey());
            }*/
            System.out.println(key);
            System.out.println(value);
        }

    }

    public    static void fol2(){

        Iterator<CacheObj<String, String>> cacheObjIterator2 = fifoCache.cacheObjIterator();
        while (cacheObjIterator2.hasNext()){
            CacheObj<String, String> next = cacheObjIterator2.next();
            String key = next.getKey();
            String value = next.getValue();
        /*    if ( next.getKey()==null){
                //如果当前值为null 就表示当前缓存失效 就进行移除
                fifoCache.remove(next.getKey());
            }*/
            System.out.println(key);
            System.out.println(value);
        }


    }




    public static void main(String[] args) {

        /*字符串的切割对比*/
        String  str="nihao,caihua,pengfei,beiyang";
        TimeInterval timersstr = DateUtil.timer();
        String[] split = str.split(",");
        System.out.println(timersstr.interval());
        System.out.println(str);
        String  str2="nihao,caihua,pengfei,beiyang";
        TimeInterval timersstr2 = DateUtil.timer();
        String[] split1 = StrUtil.split(str, ",");
        System.out.println(timersstr2.interval());



        fol();
        System.out.println("nihao ");



        TimeInterval timers = DateUtil.timer();
        DateTime dateTime2=new DateTime();
        long time = dateTime2.getTime();
        Console.log(timers.interval());

        /*时间*/
        TimeInterval timerss = DateUtil.timer();
       Date date=new Date();
        long time2 = date.getTime();
        Console.log(timerss.interval());

        fol2();
    }



    public static void main8(String[] args) {
          /*时间对比 DateTime*/
        TimeInterval timers = DateUtil.timer();
        DateTime dateTime2=new DateTime();
        long time = dateTime2.getTime();
        Console.log(timers.interval());

        //Date
        TimeInterval timeInterval = DateUtil.timer();


        Console.log(timeInterval.interval());

        DateTime dateTime=new DateTime();
        System.out.println(dateTime);
        Date date = dateTime.toJdkDate();
        System.out.println(date);
        /*测试字符串性能*/
        //StringBuilder
        TimeInterval timer = DateUtil.timer();
        StringBuilder b2 = new StringBuilder();
       /* for(int i =0; i< 1000000; i++) {
            b2.append("test");
           *//* b2 = new StringBuilder();*//*
        }*/
        Console.log(timer.interval());

//StrBuilder
        TimeInterval timer2 = DateUtil.timer();
        StrBuilder builder = StrBuilder.create();
      /*  for(int i =0; i< 1000000; i++) {
            builder.append("test");
        *//*    builder.reset();*//*
        }*/
        Console.log(timer2.interval());

        List<Map> list=new ArrayList<>();
        for (int i = 0; i < 50  ; i++) {
            Map<String,String> map=new HashMap<>();
            map.put("key1", "value1");
            map.put("key2", "value2");
            map.put("key3", "value3");
            list.add(map);
        }

        /*hutool*/
        TimeInterval timer4= DateUtil.timer();
        String parse = JSONUtil.toJsonStr(list);
        /* System.out.println(parse+"没有删除的json对象2");*/
        System.out.println(timer4.interval());
        /*fastjson*/
        TimeInterval timer3= DateUtil.timer();
        String strmap = JSONObject.toJSONString(list);
        System.out.println(timer3.interval());
   /*     System.out.println(strmap+"没有删除的json对象");*/






    }
    public static void main3(String[] args) {
        String a = "217-05-06 00:00:00";




        Map<String,String> map=new HashMap<>();
      map.put("key1", "value1");
      map.put("key2", "value2");
      map.put("key3", "value3");

      /*移除key*/
      String strmap = JSONObject.toJSONString(map);
      System.out.println(strmap+"没有删除的json对象");
      String key1 = map.remove("key3");

      System.out.println(strmap+"没有删除的json对象");
      String strmap2 = JSONObject.toJSONString(map);
      System.out.println(key1+"删除的json对象");
      System.out.println(strmap2+"删除的json对象");
      Map<String, String> stringMap = MapUtil.removeAny(map, "key1");

      String str23 = JSONObject.toJSONString(stringMap);
      System.out.println(str23);

      MapProxy proxy = MapUtil.createProxy(map);

      FIFOCache<String, String> fifoCache = CacheUtil.newFIFOCache(3);
      fifoCache.put("key1", "value1", DateUnit.SECOND.getMillis() * 3);
      fifoCache.put("key2", "value2", DateUnit.SECOND.getMillis() * 6);
      fifoCache.put("key3", "value3", DateUnit.SECOND.getMillis() * 36);



        Iterator<CacheObj<String, String>> cacheObjIterator = fifoCache.cacheObjIterator();
      while (cacheObjIterator.hasNext()){
       CacheObj<String, String> next = cacheObjIterator.next();


       String key = next.getKey();
       String value = next.getValue();
       if ( next.getKey()==null){
           //如果当前值为null 就表示当前缓存失效 就进行移除
           fifoCache.remove(next.getKey());
       }
       System.out.println(key);
       System.out.println(value);
      }



      Object annotationValue = AnnotationUtil.getAnnotationValue(ZabbixControllerItem.class, RequestMapping.class);
      System.out.println(annotationValue+"注解值");


      DateTime dateTime2=new DateTime();

      System.out.println(dateTime2+"当前时间");

      DateTime yesterday = DateUtil.yesterday();
      System.out.println("昨天"+yesterday);

      String chineseDatePattern = DatePattern.CHINESE_DATE_PATTERN;

      FastDateFormat normDatetimeMinuteFormat = DatePattern.NORM_DATETIME_MINUTE_FORMAT;
      String format1 = normDatetimeMinuteFormat.format(new Date());

      String s5 = UUID.randomUUID().toString();
      System.out.println("随机数"+s5);
      System.out.println("随机数"+s5);



   DateTime dateTime=new DateTime();
      dateTime.toDateStr();
      DateTime parse1 = DateUtil.parse("2017-05-06 00:00:00.0", "yyyy-MM-dd HH:mm:ss");
      System.out.println(parse1+"时间");
      TimeInterval timer = DateUtil.timer();
      System.out.println(timer+"hua费时间");
      Date value = Convert.toDate(a);
      String s = value.toString();
      TimeInterval timer2 = DateUtil.timer();
      long l1 = timer2.intervalMs();
      System.out.println(l1+"hua费时间2");
      System.out.println(timer+"hua费时间2");
        System.out.println(s);
        System.out.println(value);

      RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
      Runtime runtime = runtimeInfo.getRuntime();
      System.out.println(runtime);

      String format = DateUtil.format(new Date(), "yyyy-HH-mm");

  /*    DateTime dateTime2 = DateUtil.beginOfDay(new Date());*/

      DateTime dateTime1=new DateTime();
      Quarter quarter = dateTime1.quarterEnum();
      System.out.println(quarter.getValue()+"Quarter  季度"+quarter.name());

      DayOfMonthValueParser dayOfMonthValueParser=new DayOfMonthValueParser();
      int parse = dayOfMonthValueParser.parse("5");
      System.out.println(parse+"每月的几号处理");



      System.out.println(dateTime);
      System.out.println(dateTime.getFirstDayOfWeek());
      //相差多少天
      long l = DateUtil.betweenDay(value, new Date(), false);
      System.out.println(l+"相差多少天");
      DateField dayOfWeek = DateField.DAY_OF_WEEK;
      int value1 = DateField.DAY_OF_WEEK.getValue();
      System.out.println(dayOfWeek);
      System.out.println(value1);
      System.out.println(dayOfWeek.toString());


      /*正则匹配*/

      String[] split = Validator.BIRTHDAY.split("");
      Pattern p = Pattern.compile("a*b");
      /*Pattern pattern=new Pattern();*/
      Matcher m = p.matcher("aaaaab");
      boolean b = m.matches();

      Pattern compile = Pattern.compile("\\p{Lower}");

      String s2 = ReUtil.delFirst("a", "bab");
      String str = ReUtil.delFirst(compile, "bab");
      System.out.println(str+"str");

      String s3 = ReUtil.delFirst(ReUtil.RE_CHINESES, "b我的b");
      Pattern citizenId = Validator.CITIZEN_ID;
      boolean s4 = ReUtil.contains(citizenId, "");
      System.out.println(s2+"s2");
      System.out.println(s3+"s3");
      System.out.println(s4+"s4");
      System.out.println(p+""+""+m+""+b+"这是什么");


      boolean abc = ReUtil.contains(p, "abc");
      System.out.println(abc);

      long current = DateUtil.current(false);
      System.out.println(current+"当前时间戳");


      ExtendsSpaceAndExpirepassword andExpirepassword=new ExtendsSpaceAndExpirepassword();
      andExpirepassword.setName("wode ");
      andExpirepassword.setValue("wode ss");
      ExtendsSpaceAndExpirepassword andExpirepassword2=new ExtendsSpaceAndExpirepassword();
      andExpirepassword2.setName("wode ");
      andExpirepassword2.setValue("wode ss");
      
      List<ExtendsSpaceAndExpirepassword> spaceAndExpirepasswords=new ArrayList<>();
      spaceAndExpirepasswords.add(andExpirepassword);
      spaceAndExpirepasswords.add(andExpirepassword2);
     String o = JSONObject.toJSONString(spaceAndExpirepasswords);

      String s1 = JSONObject.toJSONString(andExpirepassword);
      JSONObject.parseObject(s1,ExtendsSpaceAndExpirepassword.class);

   /*   反射*/
      Constructor<ChartBase>[] constructors = ReflectUtil.getConstructors(ChartBase.class);
      for (int i = 0; i < constructors.length; i++) {
          Constructor<ChartBase> constructor = constructors[i];
          System.out.println(constructor.getName()+"名字");
      }
      Method[] methods = ReflectUtil.getMethods(ZabbixControllerItem.class);
      for (int i = 0; i < methods.length; i++) {
          Method method = methods[i];
          String name = method.getName();
          System.out.println(name+"方法名字");

          Class<?>[] parameterTypes = method.getParameterTypes();

          if(parameterTypes.length==0){
              System.out.println("此方法无参数");
          }
          for (Class<?> class1 : parameterTypes) {
              String parameterName = class1.getName();
              System.out.println("参数类型："+parameterName);
          }
          System.out.println("****************************");

      }






      System.out.println(constructors+"反射");
      Constructor<?>[] constructorsDirectly = ReflectUtil.getConstructorsDirectly(ChartBase.class);

      System.out.println(constructorsDirectly.toString()+"字段列表");
      System.out.println(constructorsDirectly);

      Object[] ss= {"a", "你", "好", "", 1};
      List<?> list = Convert.convert(List.class, ss);


      //从4.1.11开始可以这么用
        List<?> list2 = Convert.toList(ss);
        System.out.println(list);
        System.out.println(list2);
    }









   /* public Oraclepojo getaLLhostbyID() throws Exception {
        List<Map<String,Object>> result = (List<Map<String, Object>>) allApi.getaLLhostbyID().getResult();
       *//* Zabbixpojo zabbixpojo=new Zabbixpojo();*//*
        Oraclepojo oraclepojo=new Oraclepojo();
        for (Map<String,Object> map:result) {
            if(map.get("host").equals("10.0.5.41")){
               *//* zabbixpojo.setHost(map.get("host").toString());*//*
                CommonResponse history = allApi.getHistory(map.get("hostid").toString(), "extendspace");
                List<Map<String,Object>> result1 = (List<Map<String, Object>>) history.getResult();
                Map<String, Object> map1 = result1.get(0);
                String clock = (String) map1.get("clock");
                Map<String,String> stringMap=new HashMap<>();
                oraclepojo.setClock(clock);
                String value = (String) map1.get("value");
                String[] split = value.split(",");
                List<ExtendsSpace> spaceList=new ArrayList<>();
               for(int i=0;i<split.length;i++){
                   String s = split[i];
                   stringMap= JSONObject.parseObject(split[i],Map.class);
                   stringMap.forEach((k,v) ->{
                       ExtendsSpace extendsSpace=new ExtendsSpace();
                       extendsSpace.setName(k);
                       extendsSpace.setValue(v);
                       spaceList.add(extendsSpace);
                   });
               }
                oraclepojo.setSpaceList(spaceList);
            }
        }
        *//*这里有个问题就是 这个服务要保证有一个oracle*//*
        return  oraclepojo;
    }*/
    /*   public Oraclepojo getaLLhostbyID(String extendspace) throws Exception {
        List<Map<String,Object>> result = (List<Map<String, Object>>) allApi.getaLLhostbyID().getResult();
        Oraclepojo oraclepojo=new Oraclepojo();
        for (Map<String,Object> map:result) {
            if(map.get("host").equals("10.0.5.41")){
                CommonResponse history = allApi.getHistory(map.get("hostid").toString(), extendspace);
                List<Map<String,Object>> result1 = (List<Map<String, Object>>) history.getResult();
                Map<String, Object> map1 = result1.get(0);
                String clock = (String) map1.get("clock");
                oraclepojo.setClock(clock);
                String value = (String) map1.get("value");
                String[] split = value.split(",");
                if(extendspace.equals("extendspace")){

                }
                List<ExtendsSpaceAndExpirepassword> spaceList = ExtendsSpace(split);


                oraclepojo.setSpaceList(spaceList);
            }
        }
        return  oraclepojo;
    }*/


   /*public Oraclepojo getaLLexpirypassword(String extendspace) throws Exception {
       List<Map<String,Object>> result = (List<Map<String, Object>>) allApi.getaLLhostbyID().getResult();
       Oraclepojo oraclepojo=new Oraclepojo();
       for (Map<String,Object> map:result) {
           if(map.get("host").equals("10.0.5.41")){
               CommonResponse history = allApi.getHistory(map.get("hostid").toString(), extendspace);
               List<Map<String,Object>> result1 = (List<Map<String, Object>>) history.getResult();
               Map<String, Object> map1 = result1.get(0);
               String clock = (String) map1.get("clock");
               oraclepojo.setClock(clock);
               String value = (String) map1.get("value");
               String[] split = value.split(",");
               List<ExtendsSpaceAndExpirepassword> spaceList = expirypassword(split);
               oraclepojo.setSpaceList(spaceList);
           }
       }
       return  oraclepojo;
   }*/


    /*            /*   long time = expirydate.getTime() - nowdate.getTime();//这样得到的差值是毫秒级别
                    long days = time / (1000 * 60 * 60 * 24);*/
               /*     long hours = (time-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
                    long minutes = (time-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);*/
    /* extendsSpace.setValue(""+days+"天"+hours+"小时"+minutes+"分");*/

}
