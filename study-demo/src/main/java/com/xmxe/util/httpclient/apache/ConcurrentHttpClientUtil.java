package com.xmxe.util.httpclient.apache;

import java.io.IOException;
import java.net.URI;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConcurrentHttpClientUtil {
    // Basic认证
    private static final CredentialsProvider credsProvider = new BasicCredentialsProvider();
    // httpClient
    private static final CloseableHttpClient httpclient;
    // httpGet方法
    private static final HttpGet httpget;
    //
    private static final RequestConfig reqestConfig;
    // 响应处理器
    private static final ResponseHandler<String> responseHandler;
    // jackson解析工具
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        System.setProperty("http.maxConnections", "50");
        System.setProperty("http.keepAlive", "true");
        // 设置basic校验
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials("", ""));
        // 创建http客户端
        httpclient = HttpClients.custom()
                .useSystemProperties()
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        // 初始化httpGet
        httpget = new HttpGet();
        // 初始化HTTP请求配置
        reqestConfig = RequestConfig.custom()
                .setContentCompressionEnabled(true)
                .setSocketTimeout(100)
                .setAuthenticationEnabled(true)
                .setConnectionRequestTimeout(100)
                .setConnectTimeout(100).build();
        httpget.setConfig(reqestConfig);
        // 初始化response解析器
        responseHandler = new BasicResponseHandler();
    }

    /*
     * 返回响应
     */
    public static String getResponse(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        String response = httpclient.execute(get, responseHandler);
        return response;
    }

    /*
     * 发送http请求,并用net.sf.json工具解析
     */
    public static JSONObject getUrl(String url) throws Exception {
        try {
            httpget.setURI(URI.create(url));
            String response = httpclient.execute(httpget, responseHandler);
            JSONObject json = JSONObject.parseObject(response);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 发送http请求,并用jackson工具解析
     */
    public static JsonNode getUrl2(String url) {
        try {
            httpget.setURI(URI.create(url));
            String response = httpclient.execute(httpget, responseHandler);
            JsonNode node = mapper.readTree(response);
            return node;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 发送http请求,并用fastjson工具解析
     * 
     */
    public static com.alibaba.fastjson.JSONObject getUrl3(String url) {
        try {
            httpget.setURI(URI.create(url));
            String response = httpclient.execute(httpget, responseHandler);
            com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(response);
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
