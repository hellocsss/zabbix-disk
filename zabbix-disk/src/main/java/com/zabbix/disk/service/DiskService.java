package com.zabbix.disk.service;

import com.alibaba.fastjson.JSONObject;
import com.zabbix.disk.config.ZabbixApiService;
import com.zabbix.disk.entity.ChartBase;
import com.zabbix.disk.entity.CommonRequest;
import com.zabbix.disk.entity.CommonResponse;
import com.zabbix.disk.entity.Disk;
import com.zabbix.disk.entity.page.Item;
import io.github.hengyunabc.zabbix.api.DefaultZabbixApi;
import io.github.hengyunabc.zabbix.api.Request;
import io.github.hengyunabc.zabbix.api.RequestBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: jwh
 * @date 2020/6/2
 */
@Service
@Component
@Slf4j
public class DiskService {
    final DefaultZabbixApi zabbixApi = new DefaultZabbixApi("http://10.0.5.172:8080/zabbix/api_jsonrpc.php");
    //DefaultZabbixApi zabbixApi = new DefaultZabbixApi("http://192.168.114.130/zabbix/api_jsonrpc.php");
    final String user = "Admin";
    final String password = "zabbix";
    //final String password="123123";

    /**
     * 根据主机名、键值/监控项名获取监控项id
     * @param mainName
     * @param keyName
     * @param itemName
     * @return
     */
    public String getItemId(String mainName, String keyName, String itemName) {
        zabbixApi.init();
        boolean loginResult = zabbixApi.login(user, password);
        if (loginResult) {
            Map<String, String> search = new HashMap<>();
            search.put("key_", keyName);
            search.put("name", itemName);
            Request request = RequestBuilder.newBuilder().method("item.get")
                    .paramEntry("output", "itemid")
                    .paramEntry("host", mainName)
                    .paramEntry("search", search).build();
            JSONObject resJson = zabbixApi.call(request);
            String regEx = "[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(String.valueOf(resJson.get("result")));
            return m.replaceAll("").trim();
        } else {
            return "";
        }
    }

    /**
     * zabbixApi获取历史数据
     * @param itemId
     * @param dataType
     * dataType-可能的值:
     * 0 - 数字浮点;
     * 1 - 字符串;
     * 2 - 日志;
     * 3 - 无符号数字;
     * 4 - 文本.
     * @return
     */
    public List<Disk> getHistoryDate(String itemId, Integer dataType, Integer dataNum) {
        Request request2 = RequestBuilder.newBuilder().method("history.get")
                .paramEntry("sortfield", "clock")
                .paramEntry("sortorder", "DESC")
                .paramEntry("history", dataType)
                .paramEntry("limit", dataNum)
                .paramEntry("itemids", itemId).build();
        JSONObject message = zabbixApi.call(request2);
        List<Disk> dataList = new ArrayList<>();
        List<Disk> dataJsonList = (List<Disk>) message.get("result");
        for (int i = 0; i < dataJsonList.size(); i++) {
            JSONObject json = JSONObject.parseObject(String.valueOf(dataJsonList.get(dataJsonList.size() - 1 - i)));
            Disk zabbixData = JSONObject.toJavaObject(json, Disk.class);
            dataList.add(zabbixData);
        }
        return dataList;
    }

    /**
     * 通用接口：根据主机名、键值/监控项名/获取zabbix数据
     * @param mainName
     * @param keyName
     * @param itemName
     * @param dataType
     * @param dataNum
     * @return
     */
    public List<Disk> getEither(String mainName, String keyName, String itemName, Integer dataType, Integer dataNum) {
        String itemId = getItemId(mainName, keyName, itemName);
        return getHistoryDate(itemId, dataType, dataNum);
    }


    @Resource
    private ZabbixApiService zabbixApiService;

    /**
     * 根据主机名返回主机id
     * @param host
     *
     * @throws Exception
     */
    public String getHostByHostId(String host)throws Exception{
        List<Map<String, Object>> result = (List<Map<String, Object>>) this.getAllHost().getResult();
        for (Map<String, Object> map : result) {
            String hostName=map.get("host").toString();
            if(hostName.equals(host)){
                return map.get("hostid").toString();
            }
        }
        return "";
    }

    /**
     * 获取历史数据
     *
     * @param hostId
     * @param item
     * @param dockerName
     * @return
     */
    public List<ChartBase> getHistory(String hostId, Item item, String dockerName) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setAuth(zabbixApiService.loginZabbix());
        request.setMethod("history.get");
        request.setParams(new HashMap(6) {{
            put("sortfield", "clock");
            put("sortorder", "DESC");
            put("history", item.getType());
            put("limit", 10);
            put("itemids", getItemIdsByItemName(hostId, item, dockerName));
            put("hostids", hostId);
        }});
        CommonResponse call = zabbixApiService.call(request);
        List<Map<String, Object>> resultMap = (List<Map<String, Object>>) call.getResult();
        List<ChartBase> result = new ArrayList<>();
        for (Map<String, Object> map : resultMap) {
            ChartBase chartBase = new ChartBase();
            chartBase.setValue(map.get("value").toString());
            chartBase.setClock(Long.parseLong(map.get("clock").toString()));
            result.add(chartBase);
        }
        return result;
    }

    public List<ChartBase>  getHistory(String hostId, Item item) throws Exception {
        return getHistory(hostId, item, "");
    }

    /**
     * 根据监控项名获取监控项id
     *
     * @param hostId
     * @param item
     * @param dockerName
     * @return
     */
    private String getItemIdsByItemName(String hostId, Item item, String dockerName) throws Exception {
        CommonRequest request = new CommonRequest();
        request.setAuth(zabbixApiService.loginZabbix());
        request.setMethod("item.get");
        request.setParams(new HashMap(3) {{
            put("output", "itemid");
            put("hostids", hostId);
            put("search", new HashMap(1) {{
                if (StringUtils.isNoneBlank(dockerName)) {
                    put("name", item.getName() + " " + dockerName);
                } else {
                    put("name", item.getName());
                }
            }});
        }});
        CommonResponse call = zabbixApiService.call(request);
        List<Map<String, Object>> result = (List<Map<String, Object>>) call.getResult();
        if (result.size() > 0) {
            return result.get(0).get("itemid").toString();
        }
        log.error(String.format("获取getItemIdsByItemName失败:hostid-%s,itemNames-%s", hostId, item.getName()));
        return "";
    }

    /**
     * 获取全部主机
     *
     * @throws Exception
     */
    public CommonResponse getAllHost() throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.get");
        Map<String, Object> params = new HashMap<>();
        List<String> hostsList = new ArrayList<>();
        hostsList.add("host");
        hostsList.add("hostId");
        params.put("output", hostsList);
        request.setParams(params);
        CommonResponse call = zabbixApiService.call(request);
        return call;
    }

    /**
     * 获取指定主机的指定键的监控项(可模糊搜索)
     *
     * @throws Exception
     */
    public CommonResponse getAllItemsByHostIdAndKey(String hostId,String keyName) throws Exception {
        //String selctItems = "[\"name\",\"itemid\"]";
        CommonRequest request = new CommonRequest();
        request.setAuth(zabbixApiService.loginZabbix());
        request.setMethod("item.get");
        request.setParams(new HashMap(3) {{
            // put("countOutput", "true");
            put("output","extend");
            put("hostids", hostId);
            //put("application", "Filesystem ");
            //put("startSearch","true");
            put("search", new HashMap(1) {{
                //put("application", "Filesystem /var");
                put("key_",keyName);
            }});
        }});
        CommonResponse call = zabbixApiService.call(request);
        return call;
    }

    /**
     * 监控项名和type列表
     *
     * @param call
     * @return
     */
    public List<Item> getItemListByCall(List<Map<String, Object>> call) throws Exception {
        List<Item> itemList = new ArrayList<>();
        for (Map<String, Object> map : call) {
            Item item=new Item();
            item.setName(map.get("name").toString());
            item.setType(map.get("value_type").toString());
            itemList.add(item);
        }
        return itemList;
    }

    /**
     * 监控项名和type键值对
     *
     * @param call
     * @return
     */
    public Map<String, Item> getItemMapByCall(List<Map<String, Object>> call) throws Exception {
        Map<String, Item> itemList = new HashMap();
        for (Map<String, Object> map : call) {
            Item item=new Item();
            item.setName(map.get("name").toString());
            item.setType(map.get("value_type").toString());
            itemList.put(item.getName(),item);
        }
        return itemList;
    }

    /**
     * 获取主机所有磁盘监控项名和type列表
     *
     * @param hostId
     * @return
     */
    public List<Item> getAllDiskItemListByHostId(String hostId) throws Exception {
        List<Map<String, Object>> result = (List<Map<String, Object>>) this.getAllItemsByHostIdAndKey(hostId,"vfs.fs").getResult();
        return this.getItemListByCall(result);
    }

    /**
     * 获取主机所有磁盘监控项名和type键值对
     *
     * @param hostId
     * @return
     */
    public Map<String, Item> getAllDiskItemMapByHostId(String hostId) throws Exception {
        List<Map<String, Object>> result = (List<Map<String, Object>>) this.getAllItemsByHostIdAndKey(hostId,"vfs.fs").getResult();
        return this.getItemMapByCall(result);
    }

    /**指定主机和磁盘监控项键值对
     *
     *
     * @return map<主机名,Item列表>
     */
    public  Map<String,List<Item>> getAllDiskHostAndItem() throws Exception {
        List<Map<String, Object>> result = (List<Map<String, Object>>) this.getAllHost().getResult();
        Map<String,List<Item>> hostItem=new HashMap();
        for (Map<String, Object> map : result) {
            hostItem.put(map.get("host").toString(),getAllDiskItemListByHostId(map.get("hostid").toString()));
        }
        return hostItem;
    }

    /**对应磁盘的最大值
     *
     *
     * @return
     */
    public String getDiskTotalByItem(String hostId,String itemName) throws Exception{
        Map<String, Item> itemMap=this.getAllDiskItemMapByHostId(hostId);
        String sizeItem=itemMap.get(itemName).getName().replace("Used","Total");
        List<ChartBase> itemData=this.getHistory(hostId,itemMap.get(sizeItem));
        return itemData.get(0).getValue();
}

}
