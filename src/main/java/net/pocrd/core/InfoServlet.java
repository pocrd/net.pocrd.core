package net.pocrd.core;

import net.pocrd.define.ConstField;
import net.pocrd.define.Serializer;
import net.pocrd.document.Document;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.CompileConfig;
import net.pocrd.util.POJOSerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 获取接口信息
 *
 * @author guankaiqiang
 */
//@WebServlet("/info.api")
public class InfoServlet extends HttpServlet {
    private static final long                 serialVersionUID       = 1L;
    private static final Logger               logger                 = LoggerFactory.getLogger(InfoServlet.class);
    private static final Serializer<Document> docs                   = POJOSerializerProvider.getSerializer(Document.class);
    private static final String               XML_RESP_CONTENT_TYPE  = "application/xml";
    private static final String               JSON_RESP_CONTENT_TYPE = "application/json";
    private static final String               RESP_CHARSET           = "UTF-8";
    private static       byte[]               XML_HEAD               = ("<?xml version='1.0' encoding='utf-8'?><?xml-stylesheet type='text/xsl' href='" + CommonConfig.getInstance().getApiInfoXslSite() + "'?>").getBytes(
            ConstField.UTF8);
    private static ApiMethodInfo[] apiMethodInfos;
    private static Document        document;
    private static Object lock = new Object();
    public static void setApiMethodInfos(final ApiMethodInfo[]... infos) {
        if (CompileConfig.isDebug) {
            synchronized (lock) {
                if (infos != null && infos.length > 0) {
                    List infoList = new LinkedList<ApiMethodInfo>();
                    for (ApiMethodInfo[] infoArray : infos) {
                        infoList.addAll(Arrays.asList(infoArray));
                    }
                    apiMethodInfos = new ApiMethodInfo[infoList.size()];
                    infoList.toArray(apiMethodInfos);
                }
                document = new ApiDocumentationHelper().getDocument(apiMethodInfos);
            }
        }
    }
    public static Document getDocument() {
        synchronized (lock) {
            return document;
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (CompileConfig.isDebug) {
            try {
                OutputStream out = resp.getOutputStream();
                resp.setCharacterEncoding(RESP_CHARSET);
                String queryString = req.getQueryString();
                if (queryString == null || queryString.isEmpty()) {
                    resp.setContentType(XML_RESP_CONTENT_TYPE);
                    out.write(XML_HEAD);//链xslt
                    docs.toXml(document, out, true);
                } else if (queryString.contains("json")) {
                    resp.setContentType(JSON_RESP_CONTENT_TYPE);
                    docs.toJson(document, out, true);
                } else if (queryString.contains("raw")) {
                    resp.setContentType(XML_RESP_CONTENT_TYPE);
                    docs.toXml(document, out, true);
                }
            } catch (Throwable t) {
                logger.error("parse xml for api info failed.", t);
                resp.getWriter().write(t.getMessage());
                t.printStackTrace(resp.getWriter());
            }
        } else {
            OutputStream out = resp.getOutputStream();
            resp.setContentType("plain/text");
            resp.setCharacterEncoding("utf-8");
            out.write(CommonConfig.getInstance().getApigwVersion().getBytes(ConstField.UTF8));
        }
    }
}

