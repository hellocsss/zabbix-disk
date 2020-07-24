package com.zabbix.disk.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.zabbix.disk.entity.CommonResponse;


import java.util.List;
import java.util.Map;

public interface ZabbixAllApiInter {

    /**
     * 获取主机所有信息
     */
    @Cached(name = "hostid",cacheType= CacheType.LOCAL)
  CommonResponse getaLLhostbyID() throws Exception;
    /**
     * 历史数据  主机id和项目名称
     */
  CommonResponse getHistory(String hostid, String keyname) throws Exception ;
    /**
     * 获取当前item基本，信息主机id和项目名称
     */
    CommonResponse getitem(String hostid, String keyname) throws Exception ;
}
