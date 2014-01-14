package net.pocrd.util;

import java.io.IOException;
import java.io.InputStream;

import net.pocrd.define.ConstField;
import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
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

public class WebRequestUtil {
    private static final int           MAX_CONNECTION_SIZE        = 50;
    private static final int           SOCKET_TIMEOUT             = 30000;
    private static final int           CONNECTION_TIMEOUT         = 3000;
    private static final int           CONNECTION_REQUEST_TIMEOUT = 30000;

    private static CloseableHttpClient hc                         = null;
    private static RequestConfig       rc                         = null;

    private static CloseableHttpClient getHttpClient() {
        if (hc == null) {
            synchronized (WebRequestUtil.class) {
                if (hc == null) {
                    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                    cm.setMaxTotal(MAX_CONNECTION_SIZE);
                    hc = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

                        @Override
                        public long getKeepAliveDuration(HttpResponse arg0, org.apache.http.protocol.HttpContext arg1) {
                            return 30000;
                        }
                    }).build();
                    rc = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT)
                            .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setExpectContinueEnabled(false).setRedirectsEnabled(false)
                            .build();
                }
            }
        }

        return hc;
    }

    public static String getResponseString(String baseUrl, String params, String cid, boolean useGzip) {
        CloseableHttpResponse resp = null;
        try {
            resp = getHttpResponse(baseUrl, params, cid, useGzip);
            return EntityUtils.toString(resp.getEntity(), ConstField.UTF8);
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",1 " + baseUrl + "?" + params, e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",2 " + baseUrl + "?" + params, e);
                }
            }
        }
    }

    public static byte[] getResponseBytes(String baseUrl, String params, String cid, boolean useGzip) {
        CloseableHttpResponse resp = null;
        try {
            resp = getHttpResponse(baseUrl, params, cid, useGzip);
            return EntityUtils.toByteArray(resp.getEntity());
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",1 " + baseUrl + "?" + params, e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",2 " + baseUrl + "?" + params, e);
                }
            }
        }
    }

    public static void fillResponse(String baseUrl, String params, String cid, boolean useGzip, ResponseFiller f) {
        CloseableHttpResponse resp = null;
        InputStream is = null;
        try {
            resp = getHttpResponse(baseUrl, params, cid, useGzip);
            is = resp.getEntity().getContent();
            f.fill(is);
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",1 " + baseUrl + "?" + params, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",2 " + baseUrl + "?" + params, e);
                }
            }
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",3 " + baseUrl + "?" + params, e);
                }
            }
        }
    }

    private static CloseableHttpResponse getHttpResponse(String baseUrl, String params, String cid, boolean useGzip) throws ClientProtocolException, IOException {
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase req = null;
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
        if (useGzip) {
            req.setHeader("Accept-Encoding", "gzip");
        }
        CloseableHttpResponse resp = null;
        resp = client.execute(req);
        int statusCode = resp.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new ReturnCodeException(ReturnCode.WEB_ACCESS_FAILED, cid + ",0 " + baseUrl + "?" + params + " code:" + statusCode);
        }
        return resp;
    }

    public static interface ResponseFiller {
        void fill(InputStream is);
    }
}
