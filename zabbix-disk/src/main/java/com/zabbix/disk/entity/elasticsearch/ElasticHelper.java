package com.zabbix.disk.entity.elasticsearch;


import com.zabbix.disk.config.EsClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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

@Slf4j
public class ElasticHelper {

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 创建客户端
     * @return
     */
    public static RestHighLevelClient client() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(EsClientConfig.HOST, Integer.parseInt(EsClientConfig.PORT), EsClientConfig.SCHEME)
                )
        );
        return client;
    }

    /**
     * 客户端方式写入数据
     *
     * @param clazz
     * @throws Exception
     */
    public static void save(String jsonStr,String indexName, Class clazz) throws Exception {
        RestHighLevelClient client = client();
        createIndex(indexName,clazz);
        try {
            IndexRequest indexRequest = new IndexRequest(indexName);
            indexRequest.source(jsonStr, XContentType.JSON);
            client.index(indexRequest, COMMON_OPTIONS);
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            client.close();
        }
    }

    /**
     * 创建索引
     *
     * @param indexName
     * @param clazz
     * @throws Exception
     */
    private static void createIndex(String indexName, Class clazz) {
        try {
            if (indexExist(indexName)) {
                log.error(" indexName={} 已经存在",indexName);
                return;
            }
            CreateIndexRequest indexRequest = new CreateIndexRequest(indexName);
            buildSetting(indexRequest);
            indexRequest.mapping(getXContentBuilder(clazz));
            CreateIndexResponse res = client().indices().create(indexRequest, RequestOptions.DEFAULT);
            if (!res.isAcknowledged()) {
                throw new RuntimeException("初始化失败");
            }
        } catch (Exception e) {
            log.error(e.toString());
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
     * 判断索引是否存在
     *
     * @param idxName
     * @return
     * @throws Exception
     */
    public static boolean indexExist(String idxName) throws Exception {
        return client().indices().exists(new GetIndexRequest(idxName), RequestOptions.DEFAULT);
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
