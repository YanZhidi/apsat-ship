package com.zkthinke.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huqijun
 * @Date: 2019/12/30 14:25
 */
@Slf4j
public class HttpClientUtils {

    private static final Integer STATUS_CODE_OK = 200;

    private static final String CONTENT_TYPE_JSON = "application/json";

    private static RequestConfig requestConfig;

    private static final CloseableHttpClient client;

    static {
        // 设置请求和传输超时时间
        // 配置信息
        requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(30000000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(30000000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(30000000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100);
        connManager.setDefaultMaxPerRoute(10);
        client = HttpClients.custom().setConnectionManager(connManager).build();
    }

    /**
     * 发送get请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String httpGet(String url) throws Exception{
        return httpGet(url, new HashMap<>());
    }


    /**
     * 发送get请求
     * @param url
     * @param hearsMap
     * @return
     * @throws Exception
     */
    public static String httpGet(String url, Map<String, String> hearsMap) throws Exception{
        log.info("请求url : " + url);
        log.info("请求header : " + hearsMap);
        // get请求返回结果
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        if(!CollectionUtils.isEmpty(hearsMap)){
            setGetHeadersByMap(request, hearsMap);
        }
        try {
            CloseableHttpResponse response = client.execute(request);
            // 请求发送成功，并得到响应
            if (STATUS_CODE_OK.equals(response.getStatusLine().getStatusCode())) {
                // 读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "utf-8");
            } else {
                log.error("get请求提交失败:" + url);
            }
        } finally {
            request.releaseConnection();
        }
        return null;
    }

    public static String httpPost(String url, JSONObject body) throws UnsupportedEncodingException {
        HttpPost post = new HttpPost(url);
        post.setConfig(requestConfig);
        post.addHeader("Content-Type", CONTENT_TYPE_JSON);
        post.setEntity(new StringEntity(JSONObject.toJSONString(body)));

        try {
            CloseableHttpResponse response = client.execute(post);
            // 请求发送成功，并得到响应
            if (STATUS_CODE_OK.equals(response.getStatusLine().getStatusCode())) {
                // 读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            } else {
                log.error("error post data to " + url + " body：" + body);
            }
        } catch (Exception var14) {
            log.error("error post data to " + url + " error message：" + var14.getMessage());
        } finally {
            post.releaseConnection();
        }
        return null;
    }

    /**
     * 设置头部信息
     * @param httpGet
     * @param hearsMap
     */
    private static void setGetHeadersByMap(HttpGet httpGet, Map<String, String> hearsMap){
        hearsMap.forEach((k, v) -> httpGet.setHeader(k, v));
    }
}