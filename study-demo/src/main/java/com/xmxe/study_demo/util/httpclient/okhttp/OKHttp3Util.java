package com.xmxe.study_demo.util.httpclient.okhttp;

public class OKHttp3Util {
    /*
     * 引入依赖
     * <dependency>
     *     <groupId>com.squareup.okhttp3</groupId>
     *     <artifactId>okhttp</artifactId>
     *     <version>4.10.0</version>
     * </dependency>
     */

    /*
     * get同步请求
     * 
     * String url = "https://www.baidu.com/";
     * OkHttpClient client = new OkHttpClient();
     * // 配置GET请求
     * Request request = new Request.Builder().url(url).get().build();
     * // 发起同步请求
     * try (Response response = client.newCall(request).execute()){
     *     // 打印返回结果
     *     System.out.println(response.body().string());
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     */

    /*
     * post表单同步请求
     * 
     * String url = "https://www.baidu.com/";
     * OkHttpClient client = new OkHttpClient();
     * // 配置POST+FORM格式数据请求
     * RequestBody body = new FormBody.Builder().add("userName", "zhangsan").add("userPwd", "123456").build();
     * Request request = new Request.Builder().url(url).post(body).build();
     * // 发起同步请求
     * try (Response response = client.newCall(request).execute()){
     *     // 打印返回结果
     *     System.out.println(response.body().string());
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     */

    /*
     * post表单+文件上传，同步请求
     * 
     * String url = "https://www.baidu.com/";
     * OkHttpClient client = new OkHttpClient();
     * // 要上传的文件
     * File file = new File("/doc/Downloads/429545913565844e9b26f97dbb57a1c3.jpeg");
     * RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"),file);
     * // 表单 + 文件数据提交
     * RequestBody multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("userName", "zhangsan").addFormDataPart("userPwd", "123456").addFormDataPart("userFile", "00.png", fileBody).build();
     * Request request = new Request.Builder().url(url).post(multipartBody).build();
     * // 发起同步请求
     * try (Response response = client.newCall(request).execute()){
     *     // 打印返回结果
     *     System.out.println(response.body().string());
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     */

    /*
     * post+json数据，同步请求
     * 
     * MediaType contentType = MediaType.get("application/json; charset=utf-8");
     * String url = "https://www.baidu.com/";
     * String json = "{}";
     * OkHttpClient client = new OkHttpClient();
     * // 配置POST+JSON请求
     * RequestBody body = RequestBody.create(contentType, json);
     * Request request = new Request.Builder().url(url).post(body).build();
     * // 发起同步请求
     * try (Response response = client.newCall(request).execute()){
     *     // 打印返回结果
     *     System.out.println(response.body().string());
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     */

    /*
     * 文件下载,同步请求
     * 
     * public static void main(String[] args) {
     *     // 目标存储文件
     *     String targetFile = "/doc/Downloads/1.png";
     *     // 需要下载的原始文件
     *     String url = "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png";
     *     OkHttpClient client = new OkHttpClient();
     *     // 配置GET请求
     *     Request request = new Request.Builder().url(url).build();
     *     // 发起同步请求
     *     try (Response response = client.newCall(request).execute()){
     *         // 获取文件字节流
     *         byte[] stream = response.body().bytes();
     *         // 写入目标文件
     *         writeFile(targetFile, stream);
     *     } catch (Exception e) {
     *         e.printStackTrace();
     *     }
     * }
     * // 写入目标文件
     * private static void writeFile(String targetFile, byte[] stream) throws IOException {
     *     String filePath = StringUtils.substringBeforeLast(targetFile, "/");
     *     Path folderPath = Paths.get(filePath);
     *     if(!Files.exists(folderPath)){
     *         Files.createDirectories(folderPath);
     *     }
     *     Path targetFilePath = Paths.get(targetFile);
     *     if(!Files.exists(targetFilePath)){
     *         Files.write(targetFilePath, stream, StandardOpenOption.CREATE);
     *     }
     * }
     */

    /**
     * 其他方式的同步请求
     * 
     * // 只需要在Request配置类中，换成put方式即可
     * Request request = new Request.Builder().url(url)
     * .put(body)
     * // .delete(body)
     * .build();
     */

    /**
     * 自定义添加请求头部
     * 
     * MediaType contentType = MediaType.get("application/json; charset=utf-8");
     * String url = "https://www.baidu.com/";
     * String json = "{}";
     * OkHttpClient client = new OkHttpClient();
     * // 配置header头部请求参数
     * Headers headers = new Headers.Builder().add("token", "11111-22222-333").build();
     * // 配置POST+JSON请求
     * RequestBody body = RequestBody.create(contentType, json);
     * Request request = new Request.Builder().url(url).headers(headers).post(body).build();
     * // 发起同步请求
     * try (Response response = client.newCall(request).execute()){
     *     // 打印返回结果
     *     System.out.println(response.body().string());
     * } catch (Exception e) {
     *     e.printStackTrace();
     * }
     */

    /*
     * 发起异步请求
     * 
     * String url = "https://www.baidu.com/";
     * OkHttpClient client = new OkHttpClient().newBuilder().build();
     * Request request = new Request.Builder().url(url).get().build();
     * // 发起异步请求
     * client.newCall(request).enqueue(new Callback() {
     *     @Override
     *     public void onFailure(Call call, IOException e) {
     *         System.out.println("请求异常 + " + e.getMessage());
     *     }
     *     @Override
     *     public void onResponse(Call call, Response response) throws IOException {
     *         System.out.println("请求完成，返回结果：" + response.body().string());
     *     }
     * });
     * 
     */

    /**
     * 全局设置超时时长
     * 
     * // 设置请求配置相关参数
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *   .connectTimeout(5, TimeUnit.SECONDS)     // 设置socket连接超时时间
     *   .readTimeout(5, TimeUnit.SECONDS)        // 设置数据读取连接超时时间
     *   .writeTimeout(5, TimeUnit.SECONDS)       // 设置数据写入连接超时时间
     *   .retryOnConnectionFailure(true)          // 是否自动重连
     *   .build();
     * 
     */

    /**
     * 全局配置连接池
     * // 设置请求配置相关参数，配置共享连接池
O    * kHttpClient client = new OkHttpClient().newBuilder()
     *   .connectionPool(new ConnectionPool(5, 10, TimeUnit.MINUTES)) // 表示最多有5个空闲连接，每个空闲连接最多保持10分钟。
     *   .build();
     */

    /**
     * 配置全局拦截器
     * 
     * // 设置请求配置相关参数，添加一个拦截器
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *   .addInterceptor(new Interceptor() {
     *       @Override
     *       public Response intercept(Chain chain) throws IOException {
     *           Request request = chain.request();
     *           System.out.println("请求前拦截，请求参数：" + request.toString());
     *           Response response = chain.proceed(request);
     *           System.out.println("请求后拦截，返回参数：" + response.toString());
     *           return response;
     *       }
     *   })
     *   .build();
     * 拦截器在实际的开发中，使用的非常频繁，比如我们想知道每个请求阶段的详细日志，OkHttp已经预制了一个日志拦截器，我们只需要添加相应的依赖包文件，即可进行打印。
     * <!--添加日志拦截器-->
     *   <dependency>
     *       <groupId>com.squareup.okhttp3</groupId>
     *       <artifactId>logging-interceptor</artifactId>
     *       <version>3.14.9</version>
     *   </dependency>
     * 在请求头部里面添加它，即可对每个阶段的请求日志进行详细打印。
     * // 日志拦截器
     * HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
     * logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
     * // 设置请求配置相关参数
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *         .addInterceptor(logInterceptor)
     *         .build();
     */

    /**
     * 全局配置缓存
     * // 设置请求配置相关参数，配置缓存文件
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *   .cache(new Cache(new File("/local/cacheDirectory"), 10 * 1024 * 1024))
     *   .build();
     * 如果我们某个接口不想走缓存，每次直接进行网络请求，可以通过在请求配置上指定CacheControl.FORCE_NETWORK，设置此次请求能使用网络，不用缓存。案例如下！
     * // 设置请求配置相关参数，全局配置缓存
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *   .cache(new Cache(new File("/local/cacheDirectory"), 10 * 1024 * 1024))
     *   .build();
     * // 配置GET请求，配置FORCE_NETWORK，强制走网路请求
     * Request request = new Request.Builder()
     *   .url(url)
     *   .cacheControl(CacheControl.FORCE_NETWORK)
     *   .build();
     */

    /**
     * 全局配置代理
     * // 配置代理服务器的ip和端口
     * Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress("192.168.1.100", 8080) // the local proxy
     * );
     * // 设置请求配置相关参数
     * OkHttpClient client = new OkHttpClient().newBuilder()
     *   .proxy(proxy)
     *   .build();
     */

    /**
     * 全局配置https
     
import okhttp3.*;
import okhttp3.internal.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class TestClientHttps {

    public static void main(String[] args)  {
        String url = "https://www.baidu.com/";

        OkHttpClient client = new OkHttpClient().newBuilder()
                .sslSocketFactory(getSSLSocketFactory(), getX509TrustManager())
                .hostnameVerifier(getHostnameVerifier())
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        // 发起同步请求
        try (Response response = client.newCall(request).execute()){
            // 打印返回结果
            System.out.println(response.body().string());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    **
     * 获取SSLSocketFactory
     *
    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, getTrustManager(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    **
     * 获取TrustManager
     *
    private static TrustManager[] getTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
        return trustAllCerts;
    }


    **
     * 获取HostnameVerifier，验证主机名
     *
    public static HostnameVerifier getHostnameVerifier() {
        HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
        return hostnameVerifier;
    }


    **
     * X509TrustManager：证书信任器管理类
     *
    public static X509TrustManager getX509TrustManager() {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            //检查客户端的证书是否可信
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }
            //检查服务器端的证书是否可信
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        return x509TrustManager;
    }
}
     */


    /**
     * 全局配置WebSocket
    
    OkHttpClient client = new OkHttpClient();
    // 服务端请求地址
    String socketServerUrl = "ws://mytodoserver.com/realtime";
    Request request = new Request.Builder().url(socketServerUrl).build();

    // connecting to a socket and receiving messages
    client.newWebSocket(request, new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            super.onFailure(webSocket, t, response);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            super.onOpen(webSocket, response);
        }
    });
     * 
     */
    
    /**
     * 完整的全局配置参数介绍
     * // 设置请求配置相关参数
    OkHttpClient client = new OkHttpClient.Builder()
        .cache(cache) // configure cache, see above
        .proxy(proxy) // configure proxy, see above
        .certificatePinner(certificatePinner) // certificate pinning, see above
        .addInterceptor(interceptor) // app level interceptor, see above
        .addNetworkInterceptor(interceptor) // network level interceptor, see above
        .authenticator(authenticator) // authenticator for requests (it supports similar use-cases as "Authorization header" earlier
        .callTimeout(10000) // default timeout for complete calls
        .readTimeout(10000) // default read timeout for new connections
        .writeTimeout(10000) // default write timeout for new connections
        .dns(dns) // DNS service used to lookup IP addresses for hostnames
        .followRedirects(true) // follow requests redirects
        .followSslRedirects(true) // follow HTTP tp HTTPS redirects
        .connectionPool(connectionPool) // connection pool used to recycle HTTP and HTTPS connections
        .retryOnConnectionFailure(true) // retry or not when a connectivity problem is encountered
        .cookieJar(cookieJar) // cookie manager
        .dispatcher(dispatcher) // dispatcher used to set policy and execute asynchronous requests
        .build();

        官方文档 https://square.github.io/okhttp/4.x/okhttp/okhttp3/-ok-http-client/-builder/
     */
}
