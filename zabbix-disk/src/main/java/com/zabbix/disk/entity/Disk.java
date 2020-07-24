package com.zabbix.disk.entity;

import lombok.Data;

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
@Data
public class Disk {
    /**
     * 获取到的值
     */
    private String value;

    /**
     * 相关item的ID
     */
    private String itemid;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getClock() {
        return clock;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    /**
     * 获取值的时间-时间戳
     */
    private String clock;

    /**
     * 获取值时的纳秒
     */
    private String ns;
}
