package com.zabbix.disk.entity.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>标题: 实体工具</p>
 * <p>描述: 用于ES API返回json的实体转换</p>
 * <p>版权: Copyright (c) 2018</p>
 * <p>公司: 智业软件股份有限公司</p>
 *
 * @version: 1.0
 * @author: YT
 * @date 2018/11/22
 */
@Slf4j
public class EntityHelper {

    // 本次启动后创建过的索引
    private static List<String> idx = new ArrayList<>();

    public static <T> List<T> Res2List(Response response, Class<T> clazz) throws IOException {
        List<T> list = new ArrayList<>();
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        JSONObject hits = jsonObject.getJSONObject("hits");
        JSONArray rows = hits.getJSONArray("hits");
        for (int i = 0; i < rows.size(); i++) {
            JSON obj = rows.getJSONObject(i).getJSONObject("_source");
            list.add((T) JSONObject.toJavaObject(obj, clazz));
        }
        return list;
    }

    public static int Res2Count(Response response) throws IOException {
        JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
        JSONObject hits = jsonObject.getJSONObject("hits");
        return Integer.parseInt(hits.get("total").toString());
    }

    public static Response post(String endpoint, String entityString) throws Exception {
        return request(endpoint, entityString, "POST");
    }

    public static Response request(String endpoint, String entityString, String method) throws Exception {
        HttpEntity entity = new NStringEntity(entityString, ContentType.APPLICATION_JSON);
        RestClient restClient = ESClientFactory.createRestClient();
        try {
            Request request = new Request(method, endpoint);
            request.setEntity(entity);
            request.setOptions(ESClientFactory.COMMON_OPTIONS);
            Response response = restClient.performRequest(request);
            return response;
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        } finally {
            restClient.close();
        }
    }

    /**
     * 高级客户端写入数据，创建索引与map
     * @param object
     * @param indexName
     * @param clazz
     * @throws Exception
     */
    public static void save(Object object, String indexName, Class clazz) throws Exception {
        RestHighLevelClient client = ESClientFactory.createClient();
        // 是否创建过索引，没有就创建
        existsCreateIndex(indexName, clazz, client);
        try {
            String objectStr = JSON.toJSONString(object);
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.source(objectStr, XContentType.JSON);
            client.index(indexRequest, ESClientFactory.COMMON_OPTIONS);
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            client.close();
        }
    }

    /**
     * 设置分片
     *
     * @param createIndexRequest
     */
    public static void buildSetting(CreateIndexRequest createIndexRequest) {
        createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0));
    }

    /**
     * 客户端方式写入数据
     * @param object
     * @throws Exception
     */
    public static void save(Object object, String indexName) throws Exception {
        RestHighLevelClient client = ESClientFactory.createClient();
        try {
            String objectStr = JSON.toJSONString(object);
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.source(objectStr, XContentType.JSON);
            client.index(indexRequest, ESClientFactory.COMMON_OPTIONS);
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            client.close();
        }
    }

    /**
     * 创建索引，性能优化版（没有创建过的才创建）
     * @param indexName
     * @param clazz
     * @param client
     * @throws Exception
     */
    private static synchronized void existsCreateIndex(String indexName, Class clazz, RestHighLevelClient client) {
        try {
            /// 避免每次都去服务器判断是否存在索引，做了一个本地缓存idx
            if (!idx.contains(indexName)) {
                // 缓存里没有
                // 服务器判断，如果没有，创建
                GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
                boolean exist = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
                idx.add(indexName);
                if (!exist) {
                    // 如果服务器没有，创建
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
                    buildSetting(createIndexRequest);
                    createIndexRequest.mapping(getXContentBuilder(clazz));
                    CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                    log.info(createIndexResponse.toString());
                }
            }
        } catch (Exception e) {
            log.error(e.toString());
        }

    }

    private static List<FieldMapping> getFieldInfo(Class clazz) {
        return getFieldInfo(clazz, null);
    }

    private static List<FieldMapping> getFieldInfo(Class clazz, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        List<FieldMapping> fieldMappingList = new ArrayList<>();
        for (Field field : fields) {
            FieldInfo fieldInfo = field.getAnnotation(FieldInfo.class);
            if (fieldInfo == null) {
                continue;
            }
            if ("object".equals(fieldInfo.type())) {
                Class fc = field.getType();
                //如果是基本数据类型
                if (fc.isPrimitive()) {
                    String name = field.getName();
                    if (StringUtils.isNotBlank(fieldName)) {
                        name = name + "." + fieldName;
                    }
                    fieldMappingList.add(new FieldMapping(name, fieldInfo.type(), fieldInfo.participle(), fieldInfo.ignoreAbove()));
                } else {
                    //判断是否为List
                    if (fc.isAssignableFrom(List.class)) {
                        System.out.println("List类型：" + field.getName());
                        //得到泛型类型
                        Type gt = field.getGenericType();
                        ParameterizedType pt = (ParameterizedType) gt;
                        Class lll = (Class) pt.getActualTypeArguments()[0];
                        fieldMappingList.addAll(getFieldInfo(lll, field.getName()));
                    } else {
                        fieldMappingList.addAll(getFieldInfo(fc, field.getName()));
                    }
                }
            } else {
                String name = field.getName();
                if (StringUtils.isNotBlank(fieldName)) {
                    name = fieldName + "." + name;
                }
                fieldMappingList.add(new FieldMapping(name, fieldInfo.type(), fieldInfo.participle(), fieldInfo.ignoreAbove()));
            }
        }
        return fieldMappingList;
    }

    private static XContentBuilder getXContentBuilder(Class clazz) throws IOException {
        XContentBuilder mapping = null;
        List<FieldMapping> fieldMappingList = getFieldInfo(clazz);

        mapping = XContentFactory.jsonBuilder()
                .startObject()
                .startObject("properties");
        for (FieldMapping info : fieldMappingList) {
            String field = info.getField();
            String dateType = info.getType();
            if (dateType == null || "".equals(dateType.trim())) {
                dateType = "string";
            }
            dateType = dateType.toLowerCase();
            int participle = info.getParticiple();
            if ("string".equals(dateType)) {
                if (participle == 0) {
                    mapping.startObject(field)
                            .field("type", "keyword")
                            .field("index", false)
                            .field("ignore_above", info.getIgnoreAbove())
                            .endObject();
                } else if (participle == 1) {
                    mapping.startObject(field)
                            .field("type", "text")
                            .field("analyzer", "ik_smart")
                            .endObject();
                } else if (participle == 2) {
                    mapping.startObject(field)
                            .field("type", "text")
                            .field("analyzer", "ik_max_word")
                            .endObject();
                }
            } else if ("datetime".equals(dateType)) {
                mapping.startObject(field)
                        .field("type", "date")
                        .field("format", "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
                        .endObject();
            } else if ("timestamp".equals(dateType)) {
                mapping.startObject(field)
                        .field("type", "date")
                        .field("format", "strict_date_optional_time||epoch_millis")
                        .endObject();
            } else if ("float".equals(dateType) || "double".equals(dateType)) {
                mapping.startObject(field)
                        .field("type", "scaled_float")
                        .field("scaling_factor", 100)
                        .endObject();
            } else {
                mapping.startObject(field)
                        .field("type", dateType)
                        .field("index", true)
                        .endObject();
            }
        }
        mapping.endObject()
                .endObject();
        return mapping;
    }
}
