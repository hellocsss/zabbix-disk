package com.zabbix.disk.entity.elasticsearch;


import com.zabbix.disk.config.EsConfig;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * <p>标题:  Elasticsearch 工厂类</p>
 * <p>描述: </p>
 * <p>版权: Copyright (c) 2018</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: zhx
 * @date: 2018/03/13
 */
public class ESClientFactory {

    private static final String HOST = EsConfig.HOST;
    private static final int PORT = Integer.parseInt(EsConfig.PORT);

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 创建RestHighLevelClient客户端实体
     *
     * @return Client实体
     */
    public static RestHighLevelClient createClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(HOST, PORT)));
    }

    /**
     * 创建RestClient客户端实体
     *
     * @return Client实体
     */
    public static RestClient createRestClient() {
        return RestClient.builder(new HttpHost(EsConfig.HOST, Integer.parseInt(EsConfig.PORT)))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        return requestConfigBuilder.setConnectTimeout(5000)
                                .setSocketTimeout(1000 * 60 * 30);
                    }
                }).build();
    }
}
