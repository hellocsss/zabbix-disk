package com.zabbix.disk.result.page;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: cwk
 * @date 2020/4/10
 */
@Data
public class PageInfo<T> {

    /**
     * 当前页
     */
    private int currentPage;

    /**
     * 每页显示数
     */
    private int pageSize;

    /**
     * 总条数
     * **/
    private int totalRow;

    /**
     * 返回值
     * **/
    private List<T> rows = new ArrayList<T>();

}
