package net.pocrd.util;

import java.io.IOException;
import java.io.InputStream;

import net.pocrd.define.CompileConfig;
import net.pocrd.define.ConstField;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebRequestHelper {
    private static final Logger        logger                     = LogManager.getLogger(WebRequestHelper.class);
    private static final int           MAX_CONNECTION_SIZE        = 50;
    private static final int           SOCKET_TIMEOUT             = 30000;
    private static final int           CONNECTION_TIMEOUT         = 3000;
    private static final int           CONNECTION_REQUEST_TIMEOUT = 30000;

    private static CloseableHttpClient hc                         = null;
    private static RequestConfig       rc                         = null;

    private static CloseableHttpClient getHttpClient() {
        if (hc == null) {
            synchronized (WebRequestHelper.class) {
                if (hc == null) {
                    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                    cm.setMaxTotal(MAX_CONNECTION_SIZE);
                    hc = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

                        @Override
                        public long getKeepAliveDuration(HttpResponse arg0, org.apache.http.protocol.HttpContext arg1) {
                            return 30000;
                        }
                    }).build();
                    rc = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT)
                            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setExpectContinueEnabled(false).setRedirectsEnabled(false)
                            .build();
                }
            }
        }

        return hc;
    }

    public static String getResponseString(String baseUrl, String params, String cid) throws ClientProtocolException, IOException {
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase req = null;
        if (CompileConfig.isDebug) {
            logger.info("access url(" + cid + ") : " + baseUrl + "?" + params);
        }
        if (params == null) {
            req = new HttpGet(baseUrl);
        } else if (params.length() > 200) {
            HttpPost post = new HttpPost(baseUrl);
            StringEntity se = new StringEntity(params, ConstField.UTF8);
            se.setContentType("application/x-www-form-urlencoded");
            post.setEntity(se);
            req = post;
        } else {
            req = new HttpGet(baseUrl + "?" + params);
        }
        req.setConfig(rc);
        if (CommonConfig.Instance.useHttpGzip) {
            req.setHeader("Accept-Encoding", "gzip");
        }
        if (cid != null && cid.length() > 0) {
            req.setHeader("cid", cid);
        }
        CloseableHttpResponse resp = null;
        try {
            resp = client.execute(req);
            return EntityUtils.toString(resp.getEntity(), ConstField.UTF8);
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    public static byte[] getResponseBytes(String baseUrl, String params, String cid) throws ClientProtocolException, IOException {
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase req = null;
        if (CompileConfig.isDebug) {
            logger.info("access url(" + cid + ") : " + baseUrl + "?" + params);
        }
        if (params == null) {
            req = new HttpGet(baseUrl);
        } else if (params.length() > 200) {
            HttpPost post = new HttpPost(baseUrl);
            StringEntity se = new StringEntity(params, ConstField.UTF8);
            se.setContentType("application/x-www-form-urlencoded");
            post.setEntity(se);
            req = post;
        } else {
            req = new HttpGet(baseUrl + "?" + params);
        }
        req.setConfig(rc);
        if (CommonConfig.Instance.useHttpGzip) {
            req.setHeader("Accept-Encoding", "gzip");
        }
        CloseableHttpResponse resp = null;
        try {
            resp = client.execute(req);
            return EntityUtils.toByteArray(resp.getEntity());
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }

    public static void fillResponse(String baseUrl, String params, String cid, ResponseFiller f) throws ClientProtocolException, IOException {
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase req = null;
        if (CompileConfig.isDebug) {
            logger.info("access url(" + cid + ") : " + baseUrl + "?" + params);
        }
        if (params == null) {
            req = new HttpGet(baseUrl);
        } else if (params.length() > 200) {
            HttpPost post = new HttpPost(baseUrl);
            StringEntity se = new StringEntity(params, ConstField.UTF8);
            se.setContentType("application/x-www-form-urlencoded");
            post.setEntity(se);
            req = post;
        } else {
            req = new HttpGet(baseUrl + "?" + params);
        }
        req.setConfig(rc);
        if (CommonConfig.Instance.useHttpGzip) {
            req.setHeader("Accept-Encoding", "gzip");
        }
        CloseableHttpResponse resp = null;
        InputStream is = null;
        try {
            resp = client.execute(req);
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                String url = baseUrl + "?" + params;
                logger.warn("Api access failed. httpcode:" + statusCode + "  url=" + url);
                throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, url + " code:" + statusCode);
            }
            is = resp.getEntity().getContent();
            f.fill(is);
        } finally {
            if (is != null) {
                is.close();
            }
            if (resp != null) {
                resp.close();
            }
        }
    }

    public static interface ResponseFiller {
        void fill(InputStream is);
    }
}
