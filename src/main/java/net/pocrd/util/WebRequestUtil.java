package net.pocrd.util;

import net.pocrd.define.ConstField;
import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ReturnCodeException;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class WebRequestUtil {
    private static final Logger logger                     = LoggerFactory.getLogger("httplog");//与logback中配置的logname对应
    private static final int    MAX_CONNECTION_SIZE        = 50;
    private static final int    SOCKET_TIMEOUT             = 30000;
    private static final int    CONNECTION_TIMEOUT         = 3000;
    private static final int    CONNECTION_REQUEST_TIMEOUT = 30000;

    private static CloseableHttpClient hc = null;
    private static RequestConfig       rc = null;

    private static CloseableHttpClient getHttpClient() {
        if (hc == null) {
            synchronized (WebRequestUtil.class) {
                if (hc == null) {
                    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
                    cm.setMaxTotal(MAX_CONNECTION_SIZE);
                    cm.setDefaultMaxPerRoute(MAX_CONNECTION_SIZE);
                    cm.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Consts.UTF_8).build());
                    hc = HttpClients.custom().setConnectionManager(cm).setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {

                        @Override
                        public long getKeepAliveDuration(HttpResponse arg0,
                                org.apache.http.protocol.HttpContext arg1) {
                            return 30000;
                        }
                    }).build();
                    rc = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).setSocketTimeout(SOCKET_TIMEOUT).setConnectTimeout(
                            CONNECTION_TIMEOUT).setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setExpectContinueEnabled(
                            false).setRedirectsEnabled(false).build();
                }
            }
        }

        return hc;
    }

    public static String getResponseString(String baseUrl, String params, boolean useGzip) {
        CloseableHttpResponse resp = null;
        try {
            resp = getHttpResponse(baseUrl, params, useGzip);
            String result = EntityUtils.toString(resp.getEntity(), ConstField.UTF8);
            logger.info(baseUrl + " " + params + " RESPONSE:" + result);
            return result;
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "1 " + baseUrl + " " + params, e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "2 " + baseUrl + " " + params, e);
                }
            }
        }
    }

    public static byte[] getResponseBytes(String baseUrl, String params, boolean useGzip) {
        CloseableHttpResponse resp = null;
        try {
            resp = getHttpResponse(baseUrl, params, useGzip);
            return EntityUtils.toByteArray(resp.getEntity());
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "1 " + baseUrl + " " + params, e);
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "2 " + baseUrl + " " + params, e);
                }
            }
        }
    }

    public static void fillResponse(String baseUrl, String params, boolean useGzip, ResponseFiller f) {
        CloseableHttpResponse resp = null;
        InputStream is = null;
        try {
            resp = getHttpResponse(baseUrl, params, useGzip);
            is = resp.getEntity().getContent();
            f.fill(is);
        } catch (ReturnCodeException rce) {
            throw rce;
        } catch (Exception e) {
            throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "1 " + baseUrl + " " + params, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "2 " + baseUrl + " " + params, e);
                }
            }
            if (resp != null) {
                try {
                    resp.close();
                } catch (Exception e) {
                    throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, "3 " + baseUrl + " " + params, e);
                }
            }
        }
    }

    private static CloseableHttpResponse getHttpResponse(String baseUrl, String params, boolean useGzip) throws IOException {
        CloseableHttpClient client = getHttpClient();
        HttpRequestBase req = null;
        CloseableHttpResponse resp = null;
        try {
            if (params == null) {
                req = new HttpGet(baseUrl);
            } else if (params.length() > 200) {
                HttpPost post = new HttpPost(baseUrl);
                StringEntity se = new StringEntity(params, ConstField.UTF8);
                se.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
                post.setEntity(se);
                req = post;
            } else {
                req = new HttpGet(baseUrl + "?" + params);
            }
            req.setConfig(rc);
            if (useGzip) {
                req.setHeader("Accept-Encoding", "gzip");
            }
            resp = client.execute(req);
        } catch (Exception e) {
            if (req != null) {
                req.abort();
            }
            throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, baseUrl + " " + params, e);
        }

        if (resp != null) {
            int statusCode = resp.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                if (req != null) {
                    req.abort();
                }
                throw new ReturnCodeException(ApiReturnCode.WEB_ACCESS_FAILED, baseUrl + " " + params + " code:" + statusCode);
            }
        }
        return resp;
    }

    public static interface ResponseFiller {
        void fill(InputStream is);
    }
}
