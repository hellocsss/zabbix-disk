package com.zabbix.disk.config;

import com.zabbix.disk.entity.CommonRequest;
import com.zabbix.disk.entity.CommonResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;

/**
 * <p>标题: Zabbix API调用入口</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2020</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: cwk
 * @date 2020/3/27
 */
@Component
public class ZabbixApiService {
    private volatile String auth;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 从配置文件中获取zabbix地址
     */
   /* @Value("${zabbix.url}")
    private String zabbixUrl;

    @Value("${zabbix.username}")
    private String username;

    @Value("${zabbix.password}")
    private String password;*/

    private  String zabbixUrl = "10.0.5.136:8080";
    private  String username = "Admin";
    private  String password = "zabbix";

    /**
     * url后缀
     */
    private final String API_ENDPOINT = "/api_jsonrpc.php";

    /**
     * 请求头
     */
    private static final HttpHeaders headers = new HttpHeaders();

    static {
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    /**
     * Zabbix Api统一调用入口
     *
     * @param commonRequest
     * @return
     * @throws Exception
     */
    public CommonResponse call(CommonRequest commonRequest) throws Exception {
        HttpEntity<CommonRequest> httpEntity = new HttpEntity<>(commonRequest, headers);
        String requestUrl = "http://" + zabbixUrl + API_ENDPOINT;
        System.out.println("***************Zabbix请求路径*************：" + requestUrl);
        System.out.println("***************Zabbix请求参数*************：" + commonRequest.toString());
        try {
            ResponseEntity<CommonResponse> response = restTemplate.postForEntity(requestUrl, httpEntity, CommonResponse.class);
            if (response.getStatusCodeValue() != 200) {
                throw new RuntimeException("Zabbix调用报错，状态码：" + response.getStatusCodeValue());
            }
            if (!response.hasBody()) {
                throw new RuntimeException("Zabbix调用返回Body为空");
            }
            CommonResponse body = response.getBody();
            return body;
        } catch (Exception e) {
            throw new RuntimeException("请求zabbix服务器出错", e);
        }
    }

    /**
     * 登录zabbix服务器 获取auth值
     *
     * @return
     * @throws Exception
     */
    public String loginZabbix() throws Exception {
        if (StringUtils.isBlank(this.auth)) {
            CommonRequest commonRequest = new CommonRequest();
            commonRequest.setMethod("user.login");
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("user", username);
            userInfo.put("password", password);
            commonRequest.setParams(userInfo);
            CommonResponse response = call(commonRequest);
            this.auth = response.getResult().toString();
        }
        return this.auth;
    }




}
