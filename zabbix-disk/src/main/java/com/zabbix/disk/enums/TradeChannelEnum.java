package com.zabbix.disk.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum TradeChannelEnum {
    /**
     * 线上交易
     */
    ONLINE("20","1"),

    /**
     * 线下交易
     */
    OFFLINE("10","0");

    private String value;
    private String status;

    TradeChannelEnum(String value ,String status) {
        this.value = value;
        this.status = status;
    }

    public static final Map<String, TradeChannelEnum> ALL_ENTRIES = new HashMap<>(TradeChannelEnum.values().length);

    static {
        for (TradeChannelEnum each : TradeChannelEnum.values()) {
            ALL_ENTRIES.put(each.getValue(), each);
        }
    }
    //序列化
    @JsonValue
    public String getValue() {
        return value;
    }

    // 反序列
    @JsonCreator
    public TradeChannelEnum fromValue(String value) {

        return ALL_ENTRIES.get(value);
    }
}