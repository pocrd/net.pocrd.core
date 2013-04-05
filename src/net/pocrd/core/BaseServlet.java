package net.pocrd.core;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.entity.ApiContext;
import net.pocrd.util.ClassUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public abstract class BaseServlet extends HttpServlet {
    private static final long     serialVersionUID = 1L;
    private static final Logger   logger           = LogManager.getLogger(BaseServlet.class.getName());
    protected static final Logger access           = LogManager.getLogger("net.pocrd.api.access");
    public static final Charset   utf8;
    private static ObjectMapper   json             = new ObjectMapper();
    private static ApiManager     apiManager;

    static {
        utf8 = Charset.forName("utf-8");
        json.setVisibility(PropertyAccessor.SETTER, Visibility.PUBLIC_ONLY);
        json.addMixInAnnotations(com.google.protobuf.GeneratedMessage.class, Mixin1.class);
        json.addMixInAnnotations(com.google.protobuf.MessageOrBuilder.class, Mixin2.class);
        json.setFilters(new SimpleFilterProvider().addFilter("global", SimpleBeanPropertyFilter.serializeAllExcept("unknownFields",
                "defaultInstanceForType", "descriptorForType", "allFields", "serializedSize", "initialized")));

    }

    @JsonFilter("global")
    static interface Mixin1 {

    }

    @JsonIgnoreType()
    static interface Mixin2 {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        OutputStream out = null;
        String req = null;
        try {
            ApiContext apiContext = ApiContext.getCurrent();
            ParseMethodInfo(apiContext, request);
            response.setContentType("application/json");
            out = response.getOutputStream();
            // Api_Response.Builder resp = Api_Response.newBuilder();
            // resp.setSystime(System.currentTimeMillis() / 1000);
            // Api_CallStatus.Builder callState = Api_CallStatus.newBuilder();
            // callState.setCode(ApiCode.SUCCESS.getValue());
            // callState.setLength(100);
            // callState.setMsg("hello");
            // callState.setStart(0);
            // resp.addStatus(callState);
            // json.writeValue(out, resp.build());
        } catch (Exception e) {
            logger.error("execute error.", e);
        } finally {
            if (out != null) {
                out.close();
            }
            access.info(req == null ? "" : req);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    public static void registerAll(String packageName) {
        apiManager = new ApiManager(packageName);
    }

    public abstract void ParseMethodInfo(ApiContext context, HttpServletRequest request);
}
