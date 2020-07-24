package com.zabbix.disk.controller.zabbix;


import com.zabbix.disk.entity.Oracle.Oraclepojo;
import com.zabbix.disk.enums.ZabbixOracleItem;
import com.zabbix.disk.service.CallZabbixOracleApiInter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class CallZabbixOracleApiController {
    @Resource
    private CallZabbixOracleApiInter callZabbixOracleApiInter;
    @RequestMapping("/GetALLHostByID")
    @ResponseBody
    public void  GetALLHostByID() throws Exception {
        Oraclepojo extendspace = callZabbixOracleApiInter.extendSpaceAndExpiryPassword(ZabbixOracleItem.EXTENT_SPACE.getName());
        Oraclepojo expirypassword = callZabbixOracleApiInter.extendSpaceAndExpiryPassword(ZabbixOracleItem.EXPIRY_PASSWORD.getName());
    }


}
