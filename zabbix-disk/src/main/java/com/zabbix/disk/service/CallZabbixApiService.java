package com.zabbix.disk.service;


import com.zabbix.disk.config.ZabbixApiService;
import com.zabbix.disk.entity.CommonRequest;
import com.zabbix.disk.entity.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

/**
 * <p>标题: zabbix调用 业务代码</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: cwk
 * @date 2020/4/2
 */
@Service
@Slf4j
public class CallZabbixApiService {

    @Resource
    private ZabbixApiService zabbixApiService;

    /**
     * 获取主机信息
     *
     * @param hostId
     * @return
     */
    public Map<String, Object> getHostInfoByHostId(String hostId) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.get");
        Map<String, Object> params = new HashMap() {{
            put("hostids", hostId);
            put("selectParentTemplates", new ArrayList() {{
                add("name");
                add("templateid");
            }});
        }};
        request.setParams(params);
        CommonResponse call = zabbixApiService.call(request);
        // 返回值
        Map<String, Object> returnMap = new HashMap<>();
        returnMap.put("hostId", hostId);
        List<Map<String, Object>> result = (List<Map<String, Object>>) call.getResult();
        for (Map<String, Object> map : result) {
            String hostName = (String) map.get("host");
            returnMap.put("hostName", hostName);
            String status = (String) map.get("status");
            returnMap.put("status", status);
            String zbx_available = (String) map.get("available");
            String jmx_available = (String) map.get("jmx_available");
            returnMap.put("zbx_available", zbx_available);
            returnMap.put("jmx_available", jmx_available);
            List<Map<String, String>> parentTemplates = (List<Map<String, String>>) map.get("parentTemplates");
            List<String> tempList = new ArrayList<>();
            for (Map<String, String> templateMap : parentTemplates) {
                String name = templateMap.get("name");
                tempList.add(name);
            }
            returnMap.put("templateName", tempList);
        }
        return returnMap;
    }

    /**
     * 创建主机
     *
     * @param hostIp
     * @param groupId 分组
     * @param type    类型，ZBX/JMX/SNMP
     * @param port    端口：每个agent端口不一样，docker：10050, 其他自定义
     * @return 返回创建后得主机Id
     * @throws Exception
     */
    public List<String> createZabbixHost(String hostIp, String type, int groupId, int zabbixInterfaceType, int port) throws Exception {
        log.info("********************创建主机开始***********************");
        List<String> hostids = null;
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.create");
        // 所属组
        List<Object> groupList = new ArrayList<>();
        Map<String, Object> group = new HashMap<>();
        group.put("groupid", groupId);
        groupList.add(group);

        // interface信息
        List<Object> interfacesList = new ArrayList<>();
        Map<String, Object> interfaces = new HashMap<>();
        interfaces.put("type", zabbixInterfaceType);
        interfaces.put("main", 1);
        interfaces.put("useip", 1);
        interfaces.put("ip", hostIp);
        interfaces.put("port", port);
        interfaces.put("dns", "");
        interfacesList.add(interfaces);

        // 拼接参数
        Map<String, Object> hostParam = new HashMap();
        hostParam.put("host", hostIp + "-" + type);
        hostParam.put("interfaces", interfacesList);
        hostParam.put("groups", groupList);
        request.setParams(hostParam);

        CommonResponse call = zabbixApiService.call(request);
        if (call.getError() != null) {
            if (call.getError().toString().contains("already exists.")) {
                log.info("主机:" + hostIp + "监控已存在，不可重复添加");
            }
        } else {

            Map<String, Object> result = (Map<String, Object>) call.getResult();
            hostids = (List<String>) result.get("hostids");
        }
        log.info("********************创建主机结束，主机ID为：" + hostids);
        return hostids;
    }


    /**
     * 修改主机
     *
     * @param hostId
     * @throws Exception
     */
    public void updateZabbixHost(String hostId, String serverIp) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.update");
        // 修改主机IP
        Map<String, Object> params = new HashMap<>();
        params.put("hostid", hostId);
        params.put("host", serverIp);
        request.setParams(params);
        CommonResponse callResult = zabbixApiService.call(request);
        if (callResult.getError() != null) {
            log.info("******************修改主机信息失败**********************");
        }
        // 如果修改IP地址， 更新agent信息
        updateHostInterface(hostId, serverIp);
    }

    /**
     * 根据主机id获取接口信息
     *
     * @param hostId
     * @throws Exception
     */
    public List<Map<String, String>> getHostInterfaceInfo(String hostId) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("hostinterface.get");

        Map<String, String> param = new HashMap<>();
        param.put("output", "extend");
        param.put("hostids", hostId);
        request.setParams(param);

        CommonResponse callResult = zabbixApiService.call(request);
        if (callResult.getError() != null) {
            log.info("*****************获取主机接口信息失败，错误内容：" + callResult.getError());
            return null;
        } else {
            return (List<Map<String, String>>) callResult.getResult();
        }
    }

    /**
     * 创建主机接口,
     * 通过这个接口创建的agent代理接口为辅助接口即main（默认值只能一个）值为0，其他类型main为1
     *
     * @param hostId
     * @param ip
     * @param port
     * @param type
     * @param
     * @throws Exception
     */
    /*public void createHostInterface(String hostId, String ip, int port, int type) throws Exception {
        HostInterface hostInterface = new HostInterface();
        hostInterface.setIp(ip);
        hostInterface.setPort(port);
        hostInterface.setType(type);
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("hostinterface.create");

        // interface接口信息
        Map<String, Object> interfaces = new HashMap<>();
        interfaces.put("hostid", hostId);
        interfaces.put("type", hostInterface.getType());
        interfaces.put("main", hostInterface.getMain());
        interfaces.put("useip", hostInterface.getUseIp());
        interfaces.put("ip", hostInterface.getIp());
        interfaces.put("port", hostInterface.getPort());
        interfaces.put("dns", hostInterface.getDns());
        request.setParams(interfaces);
        CommonResponse callResult = zabbixApiService.call(request);
        if (callResult.getError() != null) {
            log.info("*****************创建主机接口信息失败，错误内容：" + callResult.getError());
        } else {
            log.info("*****************创建主机接口信息成功，返回主机接口id为：" + callResult.getResult());
        }
    }*/

    /**
     * 修改主机接口信息
     *
     * @param hostId
     * @throws Exception
     */
    public void updateHostInterface(String hostId, String ip) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("hostinterface.update");

        // 获取原主机信息
        List<Map<String, String>> hostInterfaceInfoList = getHostInterfaceInfo(hostId);
        for (Map<String, String> hostInterfaceInfo : hostInterfaceInfoList) {
            String main = hostInterfaceInfo.get("main");
            if (!StringUtils.isBlank(main) && main.equals("1")) {
                // 只更新主接口
                Map<String, Object> param = new HashMap<>();
                param.put("interfaceid", hostInterfaceInfo.get("interfaceid"));
                param.put("ip", ip);
                request.setParams(param);
                CommonResponse callResult = zabbixApiService.call(request);
                System.out.println(callResult);
            }
        }
    }


    /**
     * 删除主机
     *
     * @param hostids
     * @throws Exception
     */
    public boolean deleteZabbixHost(List<String> hostids) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        boolean result = true;
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.delete");
        List<String> hostList = new ArrayList<>();
        for (String hostid : hostids) {
            hostList.add(hostid);
        }
        request.setParams(hostList);
        CommonResponse callResult = zabbixApiService.call(request);
        if (callResult.getError() != null) {
            result = false;
        }
        return result;
    }

    /**
     * 主机关联模板(一个模板可以关联多个主机)
     *
     * @param templateId
     * @param hostIds
     */
    public boolean templateMassAdd(String templateId, List<String> hostIds) throws Exception {
        log.info("************************关联模板开始****************************");
        boolean result = true;
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("template.massadd");

        Map<String, Object> params = new HashMap<>();
        // 模板Id
        List<Map<String, String>> tempsList = new ArrayList<>();
        Map<String, String> templateMap = new HashMap<>();
        templateMap.put("templateid", templateId);
        tempsList.add(templateMap);
        params.put("templates", tempsList);

        // 主机Id
        List<Map<String, String>> hostsList = new ArrayList<>();
        for (String hostId : hostIds) {
            Map<String, String> hostIdMap = new HashMap<>();
            hostIdMap.put("hostid", hostId);
            hostsList.add(hostIdMap);
        }
        params.put("hosts", hostsList);

        request.setParams(params);
        CommonResponse call = zabbixApiService.call(request);
        if (call.getError() != null) {
            result = false;
        }
        return result;
    }

    /**
     * 取消关联模板
     *
     * @param templateId
     * @param hostIds
     * @throws Exception
     */
    public boolean templateMassRemove(String templateId, List<String> hostIds) throws Exception {
        boolean result = true;
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("host.massremove");

        Map<String, Object> params = new HashMap<>();
        // 模板
        params.put("templateids_clear", templateId);
        // 主机Id
        List<String> hostsList = new ArrayList<>();
        for (String hostId : hostIds) {
            hostsList.add(hostId);
        }
        params.put("hostids", hostsList);
        request.setParams(params);

        CommonResponse call = zabbixApiService.call(request);
        if (call.getError() != null) {
            result = false;
        }
        return result;
    }

    /**
     * 获取全部主机
     *
     * @throws Exception
     */
    public void getAllHost() throws Exception {
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
        System.out.println(call);
    }

    /**
     * 获取指定主机的所有监控项
     *
     * @throws Exception
     */
    public CommonResponse getAllHostItems(String hostId) throws Exception {
        String selctItems = "[\"name\",\"itemid\"]";
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
                put("key_","vfs.fs");
            }});
        }});
        CommonResponse call = zabbixApiService.call(request);
        return call;
    }

    /**
     * 导入xml格式模板,原则上zabbix所有的导入都可以走这个接口（只测试了模板导入）
     *
     * @param sourceXml xml文件生成的字符串
     * @return true: 导入成功
     */
    public boolean importConfiguration(String sourceXml) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("configuration.import");

        // createMissing - (boolean) 如果设置为 true，新的模板将会被创建；默认：false;
        Map<String, Boolean> createMap = new HashMap<>();
        createMap.put("createMissing", true);

        // updateExisting - (boolean) 如果设置为 true，已有的模板将会被更新； 默认：false
        Map<String, Boolean> updateMap = new HashMap<>();
        updateMap.put("updateExisting", true);

        // deleteMissing - (boolean) 如果设置为 true， 不在导入数据中的监控项将会从数据库中被删除；默认：false
        Map<String, Boolean> deleteMap = new HashMap<>();
        deleteMap.put("deleteMissing", true);

        Map createAndUpdate = new HashMap() {{
            putAll(createMap);
            putAll(updateMap);
        }};
        Map createAndDelete = new HashMap() {{
            putAll(createMap);
            putAll(deleteMap);
        }};
        Map createAndUpdateAndDelete = new HashMap() {{
            putAll(createAndUpdate);
            putAll(deleteMap);
        }};

        Map<String, Object> rules = new HashMap<>();

        //设置 更新 删除 新增 规则，若不设true ，zabbix后台将不会做任何改动
        rules.put("groups", createMap);
        rules.put("templates", createAndUpdate);
        rules.put("applications", createAndDelete);
        rules.put("items", createAndUpdateAndDelete);
        rules.put("discoveryRules", createAndUpdateAndDelete);
        rules.put("triggers", createAndUpdateAndDelete);
        rules.put("graphs", createAndUpdateAndDelete);
        rules.put("httptests", createAndUpdate);
        rules.put("valueMaps", createAndUpdate);

        Map<String, Object> params = new HashMap<>();
        params.put("format", "xml");
        params.put("rules", rules);
        params.put("source", sourceXml);
        request.setParams(params);
        // 调用接口导入模板
        CommonResponse call = zabbixApiService.call(request);
        System.out.println(call.getResult());
        return Boolean.valueOf(call.getResult().toString());
    }

    /**
     * 删除模板
     *
     * @param templateIds
     * @throws Exception
     */
    public boolean deleteTemplate(List<String> templateIds) throws Exception {
        boolean result = true;
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("template.delete");
        List<String> templateList = new ArrayList<>();
        for (String templateId : templateIds) {
            templateList.add(templateId);
        }
        request.setParams(templateList);
        CommonResponse call = zabbixApiService.call(request);
        if (call.getError() != null) {
            result = false;
        }
        return result;
    }

    /**
     * 根据模板名称获取模板ID
     *
     * @param templateHostName
     * @return
     */
    public String getTemplateIdByTempateName(String templateHostName) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("template.get");

        String templateId = "";

        Map<String, String> filter = new HashMap();
        filter.put("host", templateHostName);

        Map<String, Object> params = new HashMap();
        params.put("output", "templateid");
        params.put("filter", filter);
        request.setParams(params);

        CommonResponse call = zabbixApiService.call(request);
        try {
            List templateList = (List) call.getResult();
            Map template = (Map) templateList.get(0);
            templateId = (String) template.get("templateid");
        } catch (Exception e) {
            log.error("获取templateId失败!");
        }
        return templateId;
    }

    /**
     * 查看模板信息
     *
     * @param templateName
     * @throws Exception
     */
    public Map<String, Object> getTemplateInfo(String templateName) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("template.get");
        Map<String, Object> params = new HashMap();
        Map<String, String> filter = new HashMap();

        filter.put("host", templateName);
        params.put("filter", filter);

        List<String> list = new ArrayList<>();
        list.add("tag");
        list.add("value");
        params.put("selectTags", list);

        // 查询触发器
        List<String> triggerList = new ArrayList<>();
        triggerList.add("triggerids");
        params.put("selectTriggers", triggerList);

        // 查询自动发现
        List<String> discoveryRuleList = new ArrayList<>();
        discoveryRuleList.add("hostid");
        params.put("selectDiscoveries", discoveryRuleList);

        request.setParams(params);

        CommonResponse call = zabbixApiService.call(request);
        Map<String, Object> result = new HashMap<>();
        try {
            List templateList = (List) call.getResult();
            Map<String, Object> templateInfo = (Map<String, Object>) templateList.get(0);
            result.put("templateId", templateInfo.get("templateid"));
            result.put("tags", templateInfo.get("tags"));
            result.put("name", templateInfo.get("name"));
            List<String> triggers = (List<String>) templateInfo.get("triggers");
            result.put("triggerCount", triggers.size());
            List<String> discoveries = (List<String>) templateInfo.get("discoveries");
            result.put("discoveriesCount", discoveries.size());
        } catch (Exception e) {
            log.error("获取templateId失败!");
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取指定模板得监控项数
     *
     * @param templateId
     * @throws Exception
     */
    public String getTemplateItemInfo(String templateId) throws Exception {
        String auth = zabbixApiService.loginZabbix();
        CommonRequest request = new CommonRequest();
        request.setAuth(auth);
        request.setMethod("item.get");

        Map<String, String> params = new HashMap<>();
        params.put("countOutput", "countOutput");
        params.put("templateids", templateId);
        request.setParams(params);
        CommonResponse call = zabbixApiService.call(request);

        if (call.getError() != null) {
            log.info("*******************获取指定模板id的监控项失败****************************");
            return null;
        } else {
            String result = (String) call.getResult();
            return result;
        }
    }
}
