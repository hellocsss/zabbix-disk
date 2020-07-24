package com.zabbix.disk.enums;

import lombok.Getter;

@Getter
public enum ZabbixOracleItem {
    EXTENT_SPACE("extendspace",4),
    /*expirypassword*/
    EXPIRY_PASSWORD("expirypassword",4);
    private final String name;
    private final int history;

    ZabbixOracleItem(String name, int history) {
        this.name = name;
        this.history = history;
    }

}
