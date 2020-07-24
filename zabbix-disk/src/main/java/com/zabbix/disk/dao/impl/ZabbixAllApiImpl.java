package com.zabbix.disk.dao.impl;

import com.zabbix.disk.entity.CommonRequest;
import com.zabbix.disk.entity.CommonResponse;
import com.zabbix.disk.dao.ZabbixAllApiInter;
import com.zabbix.disk.config.ZabbixApiService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Repository
public class ZabbixAllApiImpl implements ZabbixAllApiInter {
    @Resource
    private ZabbixApiService zabbixApiService;


    @Override
    public CommonResponse getaLLhostbyID() throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest commonRequest=new CommonRequest();

        commonRequest.setAuth(auth);
        commonRequest.setMethod("host.get");
        Map<String,Object> params=new HashMap<>();
        List<String> hostsList = new ArrayList<>();
        hostsList.add("host");
        hostsList.add("hostId");
        params.put("output", hostsList);
        commonRequest.setParams(params);
        CommonResponse hostrespongse = zabbixApiService.call(commonRequest);
        System.out.println("打印了几次");
        return  hostrespongse;
    }

    @Override
    public CommonResponse getHistory(String hostid, String keyname) throws Exception {
     CommonRequest commonRequest=new CommonRequest();

        commonRequest.setAuth(zabbixApiService.loginZabbix());
        commonRequest.setMethod("history.get");
        List<Map<String,Object>> getitem = (List<Map<String, Object>>) getitem(hostid, keyname).getResult();
        Map<String, Object> map = getitem.get(0);
        /* String type = (String) map.get("value_type");*/
        Map<String,Object> params=new HashMap<>();
        params.put("sortfield", "clock");
        params.put("sortorder", "DESC");
        params.put("hostids", hostid);
        params.put("history",map.get("value_type"));
        params.put("limit", 1);
        params.put("itemids",map.get("itemid"));
        commonRequest.setParams(params);
        CommonResponse historymessage = zabbixApiService.call(commonRequest);
        return  historymessage;
    }

    @Override
    public CommonResponse getitem(String hostid, String keyname) throws Exception {

        CommonRequest commonRequest=new CommonRequest();
        commonRequest.setAuth(zabbixApiService.loginZabbix());
        commonRequest.setMethod("item.get");
        Map<String,Object> params=new HashMap<>();
        params.put("output","extend");
        params.put("hostids",hostid);
        params.put("filter", new HashMap(1) {{
            //put("application", "Filesystem /var");
            put("key_",keyname);
        }});
        commonRequest.setParams(params);
        CommonResponse call = zabbixApiService.call(commonRequest);
        return call;
    }


    /*  @Override
    public List<Map<String, Object>> getitem(String hostid, String keyname) throws Exception {

        CommonRequest commonRequest=new CommonRequest();
        commonRequest.setAuth(zabbixApiService.loginZabbix());
        commonRequest.setMethod("item.get");
        Map<String,Object> params=new HashMap<>();
        params.put("output","extend");
        params.put("hostids",hostid);
        params.put("filter", new HashMap(1) {{
            //put("application", "Filesystem /var");
            put("key_",keyname);
        }});
        commonRequest.setParams(params);
        List<Map<String,Object>> result = (List<Map<String, Object>>) zabbixApiService.call(commonRequest).getResult();
        return result;
    }*/
}
