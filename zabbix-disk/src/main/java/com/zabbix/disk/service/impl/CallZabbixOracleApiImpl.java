package com.zabbix.disk.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.zabbix.disk.dao.ZabbixAllApiInter;
import com.zabbix.disk.entity.CommonResponse;
import com.zabbix.disk.entity.Oracle.ExtendsSpaceAndExpirepassword;
import com.zabbix.disk.entity.Oracle.Oraclepojo;
import com.zabbix.disk.enums.ZabbixOracleItem;
import com.zabbix.disk.service.CallZabbixOracleApiInter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class CallZabbixOracleApiImpl implements CallZabbixOracleApiInter {
    private static String HOST="10.0.5.41";
    @Resource
    private ZabbixAllApiInter zabbixAllApiInter;
    @Override
    public Oraclepojo extendSpaceAndExpiryPassword(String extendSpaceAndExpiryPassword) throws Exception {
        List<Map<String,Object>> result = (List<Map<String, Object>>) zabbixAllApiInter.getaLLhostbyID().getResult();
        Oraclepojo oraclepojo=new Oraclepojo();
        for (Map<String,Object> map:result) {
            if(map.get("host").equals(HOST)){
                /*获取历史数据*/
                CommonResponse historycode = zabbixAllApiInter.getHistory(map.get("hostid").toString(), extendSpaceAndExpiryPassword);
                List<Map<String,Object>> resultlist = (List<Map<String, Object>>) historycode.getResult();
                Map<String, Object> mapresult = resultlist.get(0);
                /* String clock = (String)map1.get("clock");*/
                oraclepojo.setClock((String) mapresult.get("clock"));
                String value = (String) mapresult.get("value");
                String[] split = value.split(",");
                if(extendSpaceAndExpiryPassword.equals(ZabbixOracleItem.EXTENT_SPACE.getName())){
                    List<ExtendsSpaceAndExpirepassword> spacelist = extendsSpaceAndExpirepassword(split,extendSpaceAndExpiryPassword);
                    oraclepojo.setSpaceexpiryList(spacelist);
                }
                if (extendSpaceAndExpiryPassword.equals(ZabbixOracleItem.EXPIRY_PASSWORD.getName())){
                    List<ExtendsSpaceAndExpirepassword> expiryList = extendsSpaceAndExpirepassword(split,extendSpaceAndExpiryPassword);
                    oraclepojo.setSpaceexpiryList(expiryList);
                }
            }
        }
        return  oraclepojo;
    }
    /**
     * split [{"aa":"ss"},{"bb":"ss"}]
     *
     */
    public static List<ExtendsSpaceAndExpirepassword>  extendsSpaceAndExpirepassword(String[] split,String extendSpaceAndExpiryPassword){
        List<ExtendsSpaceAndExpirepassword> spaceAndExpiryList=new ArrayList<>();
        Map<String,String> stringMap;
        for(int i=0;i<split.length;i++){
           stringMap= JSONObject.parseObject(split[i],Map.class);
            //hutool   fastjson
      /*      stringMap= JSONUtil.toBean(split[i],Map.class);*/

            stringMap.forEach((k,v) ->{
                ExtendsSpaceAndExpirepassword extendsSpaceAndExpirepassword =new ExtendsSpaceAndExpirepassword();
                extendsSpaceAndExpirepassword.setName(k);
                if(extendSpaceAndExpiryPassword.equals(ZabbixOracleItem.EXTENT_SPACE.getName())){
                    extendsSpaceAndExpirepassword.setValue(v);
                    spaceAndExpiryList.add(extendsSpaceAndExpirepassword);
                }
                if (extendSpaceAndExpiryPassword.equals(ZabbixOracleItem.EXPIRY_PASSWORD.getName())){
            SimpleDateFormat formattermessage = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date expirydate;
                    try {
                        expirydate = formattermessage.parse(v);
                        Date nowdate=new Date();
                        long day=(expirydate.getTime()-nowdate.getTime())/(24*60*60*1000);
                        extendsSpaceAndExpirepassword.setValue(""+day+"天");
                        spaceAndExpiryList.add(extendsSpaceAndExpirepassword);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
        /*     Date expirydate = DateUtil.parse(v, "yyyy-MM-dd HH:mm:ss");
                         long day = DateUtil.betweenDay(expirydate, new Date(), false);*/

                }

            });
        }
        return spaceAndExpiryList;
    }
}
