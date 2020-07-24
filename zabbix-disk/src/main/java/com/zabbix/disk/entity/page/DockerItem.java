package com.zabbix.disk.entity.page;

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
 * @date 2020/5/15
 */
@Getter
@Setter
public class DockerItem {
    String cpu;
    String memory;
    boolean isRunning;
}
