package com.zabbix.disk.entity;

import lombok.Data;

/**
 * <p>标题: Zabbix调用Api返回实体</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: cwk
 * @date 2020/3/27
 */
@Data
public class CommonResponse {

    private final String jsonrpc = "2.0";
    private Object result;
    private Object error;
    private String id;

}
