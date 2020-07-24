package com.zabbix.disk.enums;

import lombok.Getter;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: YT
 * @date 2020/5/17
 */
@Getter
public enum AppType {
    Java("Java"),

    Normal("Normal"),

    Redis("Redis");

    private String name;

    AppType(String name) {
        this.name = name;
    }
}
