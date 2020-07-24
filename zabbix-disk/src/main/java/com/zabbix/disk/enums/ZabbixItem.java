package com.zabbix.disk.enums;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: YT
 * @date 2020/5/14
 */
@Getter
public enum ZabbixItem {
    CpuUtilization("CPU utilization", 0),
    BitsReceived("Interface eth0: Bits received", 3),
    BitsSent("Interface eth0: Bits sent", 3),
    TotalSpace1("/etc/hostname: Total space", 0),
    UsedSpace1("/etc/hostname: Used space", 0),
    diskRead("Disk read request avg waiting time", 0),
    diskWrite("Disk write request avg waiting time", 0),
    TotalMemory("Total memory", 0),
    AvailableMemory("Available memory", 0),
    MemoryUtilization("Memory utilization", 0),
    DockerUsedRSSMemory("Used RSS memory", 3),
    Redis("Used RSS memory", 3),
    diskUseSpace("/",3)

;


    ZabbixItem(String name, int history) {
        this.name = name;
        this.history = history;
    }

    private String name;
    /**
     *     可能的值:
     *     0 - 数字浮点;
     *     1 - 字符串;
     *     2 - 日志;
     *     3 - 无符号数字;
     *     4 - 文本.
     */
    private int history;
}
