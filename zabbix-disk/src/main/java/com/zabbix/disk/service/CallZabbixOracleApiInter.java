package com.zabbix.disk.service;


import com.zabbix.disk.entity.Oracle.Oraclepojo;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author yzj
 */

public interface CallZabbixOracleApiInter {

    Oraclepojo extendSpaceAndExpiryPassword(String extendSpaceAndExpiryPassword) throws Exception;
}
