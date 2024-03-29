package com.xmxe.util.httpclient.okhttp;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpClientUtils {
    /**
     * get请求
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url, Map<String, String> headers){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .get()
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result);
    }

    /**
     * get请求（支持范型对象返回参数）
     * @param url
     * @param headers
     * @param responseType
     * @param <T>
     * @return
     */
    public static <T> T get(String url, Map<String, String> headers, TypeReference<T> responseType){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .get()
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result, responseType);
    }

    /**
     * post表单请求
     * @param url
     * @param paramMap
     * @param headers
     * @return
     */
    public static String postByForm(String url, Map<String, String> paramMap, Map<String, String> headers){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(buildFormBody(paramMap))
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result);
    }

    /**
     * post表单请求（支持范型对象返回参数）
     * @param url
     * @param paramMap
     * @param headers
     * @param responseType
     * @param <T>
     * @return
     */
    public static <T> T postByForm(String url, Map<String, String> paramMap, Map<String, String> headers, TypeReference<T> responseType){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(buildFormBody(paramMap))
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result, responseType);
    }


    /**
     * post + json请求
     * @param url
     * @param value
     * @param headers
     * @return
     */
    public static String postByJson(String url, Object value, Map<String, String> headers){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(buildJsonBody(value))
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result);
    }


    /**
     * post + json请求（支持范型对象返回参数）
     * @param url
     * @param value
     * @param headers
     * @param responseType
     * @param <T>
     * @return
     */
    public static <T> T postByJson(String url, Object value, Map<String, String> headers, TypeReference<T> responseType){
        Request request = new Request.Builder()
                .url(url)
                .headers(buildHeaders(headers))
                .post(buildJsonBody(value))
                .build();
        OkHttpClientResult result = commonRequest(request);
        return buildResponse(result, responseType);
    }


    /**
     * 包装请求头部
     * @param headers
     * @return
     */
    private static Headers buildHeaders(Map<String, String> headers){
        Headers.Builder headerBuilder = new Headers.Builder();
        if(MapUtils.isNotEmpty(headers)){
            headers.entrySet().forEach(entry -> headerBuilder.add(entry.getKey(), entry.getValue()));
        }
        return headerBuilder.build();
    }


    /**
     * 包装请求表单
     * @param paramMap
     * @return
     */
    private static FormBody buildFormBody(Map<String, String> paramMap){
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if(MapUtils.isNotEmpty(paramMap)){
            paramMap.entrySet().forEach(entry -> formBodyBuilder.add(entry.getKey(), entry.getValue()));
        }
        return formBodyBuilder.build();
    }

    /**
     * 包装请求json数据
     * @param request
     * @return
     */
    private static RequestBody buildJsonBody(Object request){
        MediaType contentType = MediaType.Companion.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.Companion.create(JacksonObjectUtil.objToJson(request),contentType);
        return requestBody;
    }

    /**
     * 包装返回结果,针对字符串
     * @param result
     * @return
     */
    private static String buildResponse(OkHttpClientResult result){
        if(!result.isSuccess() && StringUtils.isNotBlank(result.getMessage())){
            throw new RuntimeException(result.getMessage());
        }
        return byteToString(result.getBody());
    }

    /**
     * 包装返回结果,针对返回范型对象
     * @param result
     * @return
     */
    private static <T> T buildResponse(OkHttpClientResult result, TypeReference<T> responseType){
        if(!result.isSuccess() && StringUtils.isNotBlank(result.getMessage())){
            throw new RuntimeException(result.getMessage());
        }
        return JacksonObjectUtil.byteToObj(result.getBody(), responseType);
    }

    /**
     * 获取内容
     * @param bytes
     * @return
     */
    private static String byteToString(byte[] bytes){
        if(Objects.nonNull(bytes)){
            return new String(bytes, Charset.forName("utf-8"));
        }
        return StringUtils.EMPTY;
    }

    /**
     * 公共请求调用
     * @param request
     * @return
     */
    private static OkHttpClientResult commonRequest(Request request){
        return OkHttpClientBuilder.syncResponse(request);
    }
}
