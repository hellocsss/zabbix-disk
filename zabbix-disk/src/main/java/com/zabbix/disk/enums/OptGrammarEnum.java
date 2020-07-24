package com.zabbix.disk.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>标题: websocket操作类型枚举</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2018</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: cxy
 * @date: 2018/1/24
 */
public enum OptGrammarEnum {

    MKDIR("mkdir", "创建文件夹/文件", "mkdir -p %s"),
    FIND("find", "查找文件文件", "find %s -name %s"),
    RM_FILE("rmFile", "删除文件", "rm -rf %s"),
    CP("cp", "移动文件", "/bin/cp -rf %s %s"),
    TAR("tar", "解压tar", "tar -xvf %s -C %s"),
    //======================docker相关命令 start========================
    SHOW_LOG("showLog", "查看日志", "docker logs -f --tail=100 %s"),
    SHOW_DOCKER_STATUS("showDockerStatus", "查看docker状态", "systemctl is-active docker"),
    SHOW_MEMORY("showMemory", "查看内存", "docker stats --format '{{.CPUPerc}}={{.MemUsage}}={{.MemPerc}}={{.NetIO}}={{.BlockIO}}={{.PIDs}}' %s"),
    START("start", "启动容器服务", "docker start %s"),
    STOP("stop", "停止容器服务", "docker stop %s "),
    RM("rm", "删除容器", "docker rm -f %s"),
    RMI_FOR_IMAGE_ID("rmiForImageId", "删除镜像", "docker rmi %s"),
    RUN("run", "运行", "docker run -d -p %s:8080 --env HOST_IP_PORT=%s %s"),
    LOAD("load", "加载镜像", "docker load < %s"),
    GET_RUNNING_CONTAINER("get_running_container", "获取运行容器，返回容器ID", "docker ps -f name=%s --format '{{.ID}}'"),
    GET_ALL_CONTAINER("get_all_container", "获取容器，返回容器ID", "docker ps -a -f name=%s --format '{{.ID}}'"),
    ;
    //======================docker相关命令 end========================

    OptGrammarEnum() {
    }

    OptGrammarEnum(String code, String name, String command) {
        this.code = code;
        this.name = name;
        this.command = command;
    }

    /**
     * 编码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 命令
     */
    private String command;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * 获取操作语法信息
     *
     * @param code 操作编码
     * @return 操作语法对象
     * @throws Exception 异常信息
     */
    public static OptGrammarEnum getProgramCatalog(String code) throws Exception {
        if (StringUtils.isBlank(code)) {
            throw new Exception("未配置[" + code + "]操作语法信息");
        }
        for (OptGrammarEnum e : OptGrammarEnum.values()) {
            if (code.trim().toUpperCase().equals(e.getCode().toUpperCase().trim())) {
                return e;
            }
        }
        throw new Exception("未配置[" + code + "]操作语法信息");
    }
}
