package com.chenum.util;

import com.chenum.config.Config;
import com.chenum.config.CustomHttpRequestRetryHandler;
import com.chenum.config.CustomServiceUnavailableRetryStrategy;
import com.chenum.constant.Symbol;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class HttpUtil {

    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(50000)
            .setConnectionRequestTimeout(10000)
            .setSocketTimeout(50000)
            .build();
    private static CloseableHttpClient client = HttpClients.custom()
            .setMaxConnTotal(20)
            .setDefaultRequestConfig(requestConfig)
            .setRetryHandler(new CustomHttpRequestRetryHandler())
            .setServiceUnavailableRetryStrategy(new CustomServiceUnavailableRetryStrategy())
            .build();


    public static String get(String url, Map<String,Object> params,Header[] headers) throws IOException {
//        try {
            HttpGet get = new HttpGet(url + getURlParameter(params));
            get.setHeaders(headers);
            get.setHeader("Content-Type","form-data");
            CloseableHttpResponse response = client.execute(get);
            HttpEntity respEntity = response.getEntity();
            return EntityUtils.toString(respEntity, StandardCharsets.UTF_8);
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("连接失败:" + e.getMessage());
//            throw new RuntimeException("连接失败");
//        }
    }

    public static String post(String url, Map<String,Object> params) throws IOException {
        return post(url,params,null);
    }

    public static String post(String url, Map<String,Object> params, Header[] headers) throws IOException {
//        try {
            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(JsonUtil.toJsonString(params), ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            post.setHeaders(headers);
            CloseableHttpResponse response = client.execute(post);
            HttpEntity respEntity = response.getEntity();
            return EntityUtils.toString(respEntity, StandardCharsets.UTF_8);
//        }catch (IOException e){
//            e.printStackTrace();
//            System.out.println("连接失败:" + e.getMessage());
//            throw new RuntimeException("连接失败");
//        }

    }

    private static String getURlParameter(Map<String,Object> paramsMap){
        StringBuilder sb = new StringBuilder(Symbol.QUESTION);
        if (Objects.isNull(paramsMap)) {
            return sb.toString();
        }
        paramsMap.entrySet().stream().forEach(new Consumer<Map.Entry<String, Object>>() {
            @Override
            public void accept(Map.Entry<String, Object> stringObjectEntry) {
                String key = stringObjectEntry.getKey();
                String value = String.valueOf(stringObjectEntry.getValue());
                sb.append(key).append(Symbol.EQUAL).append(value).append(Symbol.AND);
            }
        });
        return sb.delete(sb.length()-1,sb.length()).toString();
    }

    public static void main(String[] args) throws IOException {
        Map<String,Object> params = new HashMap<>(2);
        params.put("username","chenum");
        params.put("password","www.chenum.com");
        String token = post(Config.get("api.request.user.admin-login"),params);
        Map<String,Object> map = JsonUtil.jsonToObject(token, Map.class);
        Map<String,Object> data = (Map<String, Object>) map.get("data");
        String chAccessToken = (String) data.get("access_token");
        Header header = new BasicHeader("ch_access_token",chAccessToken);

        Map<String,Object> params1 = new HashMap<>(2);
        params1.put("pageNum",1);
        params1.put("pageSize",20);

        get("http://localhost:8842/api/blog/admin/query/page",params1,new Header[]{header});
    }

}
