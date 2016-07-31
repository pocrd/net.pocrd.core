package net.pocrd.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class WebRequestUtil {
    private static final Logger    logger             = LoggerFactory.getLogger("httplog");//与logback中配置的logname对应
    private static final MediaType MEDIA_TYPE         = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
    private static final int       SOCKET_TIMEOUT     = 30000;
    private static final int       CONNECTION_TIMEOUT = 3000;

    private static OkHttpClient client = null;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        client = builder.build();
    }

    public static String getResponseString(String baseUrl, String params) {
        try {
            Request.Builder rb = params == null ? new Request.Builder().url(baseUrl).addHeader("Connection", "keep-alive").get() :
                    new Request.Builder().url(baseUrl).addHeader("Connection", "keep-alive").post(RequestBody.create(MEDIA_TYPE, params));
            Request request = rb.build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            logger.error("getResponseString failed.", e);
        }
        return null;
    }

    public static byte[] getResponseBytes(String baseUrl, String params) {
        try {
            Request.Builder rb = params == null ? new Request.Builder().url(baseUrl).addHeader("Connection", "keep-alive").get() :
                    new Request.Builder().url(baseUrl).addHeader("Connection", "keep-alive").post(RequestBody.create(MEDIA_TYPE, params));
            Request request = rb.build();
            Response response = client.newCall(request).execute();
            return response.body().bytes();
        } catch (Exception e) {
            logger.error("getResponseBytes failed.", e);
        }
        return null;
    }
}
