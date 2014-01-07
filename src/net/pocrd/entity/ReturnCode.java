package net.pocrd.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.pocrd.util.SparseArray;

/**
 * 基本错误码，由公共代码模块返回
 * 
 * @author rendong
 */
public class ReturnCode {
    private static SparseArray<ReturnCode> map                              = new SparseArray<ReturnCode>(100);

    public final static int                C_NO_ASSIGN                      = Integer.MIN_VALUE;
    public final static ReturnCode         NO_ASSIGN                        = new ReturnCode("NO_ASSIGN", "未分配返回值", C_NO_ASSIGN);

    public final static int                C_UNKNOWN_ERROR                  = -100;
    public final static ReturnCode         UNKNOWN_ERROR                    = new ReturnCode("UNKNOWN_ERROR", "服务端返回未知错误", C_UNKNOWN_ERROR);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int               C_INTERNAL_SERVER_ERROR          = -101;
    public final static ReturnCode         INTERNAL_SERVER_ERROR            = new ReturnCode("INTERNAL_SERVER_ERROR", C_INTERNAL_SERVER_ERROR,
                                                                                    UNKNOWN_ERROR);

    /**
     * 内部序列化异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int               C_SERIALIZE_FAILED               = -102;
    public final static ReturnCode         SERIALIZE_FAILED                 = new ReturnCode("SERIALIZE_FAILED", C_SERIALIZE_FAILED, UNKNOWN_ERROR);

    /**
     * ip受限, 对外显示为UNKNOWN_ERROR
     */
    private final static int               C_IP_DENIED                      = -103;
    public final static ReturnCode         IP_DENIED                        = new ReturnCode("IP_DENIED", C_IP_DENIED, UNKNOWN_ERROR);

    /**
     * 严重错误, 对外显示为UNKNOWN_ERROR
     */
    private final static int               C_FATAL_ERROR                    = -104;
    public final static ReturnCode         FATAL_ERROR                      = new ReturnCode("FATAL_ERROR", C_FATAL_ERROR, UNKNOWN_ERROR);

    public final static int                C_UNKNOWN_METHOD                 = -200;
    public final static ReturnCode         UNKNOWN_METHOD                   = new ReturnCode("UNKNOWN_METHOD", "mt参数服务端无法识别", C_UNKNOWN_METHOD);

    public final static int                C_PARAMETER_ERROR                = -300;
    public final static ReturnCode         PARAMETER_ERROR                  = new ReturnCode("PARAMETER_ERROR", "参数错误", C_PARAMETER_ERROR);

    public final static int                C_ACCESS_DENIED                  = -400;
    public final static ReturnCode         ACCESS_DENIED                    = new ReturnCode("ACCESS_DENIED", "访问被拒绝", C_ACCESS_DENIED);

    /**
     * 未提供token, 对外显示为ACCESS_DENIED
     */
    private final static int               C_ACCESS_DENIED_MISSING_TOKEN    = -401;
    public final static ReturnCode         ACCESS_DENIED_MISSING_TOKEN      = new ReturnCode("ACCESS_DENIED_MISSING_TOKEN",
                                                                                    C_ACCESS_DENIED_MISSING_TOKEN, ACCESS_DENIED);

    /**
     * 未达到接口所需安全级别, 对外显示为ACCESS_DENIED
     */
    private final static int               C_ACCESS_DENIED_UNMATCH_SECURITY = -402;
    public final static ReturnCode         ACCESS_DENIED_UNMATCH_SECURITY   = new ReturnCode("ACCESS_DENIED_UNMATCH_SECURITY",
                                                                                    C_ACCESS_DENIED_UNMATCH_SECURITY, ACCESS_DENIED);

    public final static int                C_SIGNATURE_ERROR                = -500;
    public final static ReturnCode         SIGNATURE_ERROR                  = new ReturnCode("SIGNATURE_ERROR", "签名错误", C_SIGNATURE_ERROR);

    public final static int                C_REQUEST_PARSE_ERROR            = -600;
    public final static ReturnCode         REQUEST_PARSE_ERROR              = new ReturnCode("REQUEST_PARSE_ERROR", "请求解析错误", C_REQUEST_PARSE_ERROR);

    /**
     * 网络访问失败, 对外显示为UNKNOWN_ERROR
     */
    private final static int C_WEB_ACCESS_FAILED = -700;
    public final static ReturnCode         WEB_ACCESS_FAILED                = new ReturnCode("WEB_ACCESS_FAILED", C_WEB_ACCESS_FAILED, UNKNOWN_ERROR);
    
    public final static int                C_SUCCESS                        = 0;
    public final static ReturnCode         SUCCESS                          = new ReturnCode("SUCCESS", "成功", C_SUCCESS);

    private final String                   name;
    private final String                   desc;
    private final int                      code;
    private final ReturnCode               shadow;
    private final static CodeInfo[]        EMPTY_CODE_INFO_ARRAY            = new CodeInfo[0];

    public static ReturnCode findCode(int c) {
        return map.get(c);
    }

    public static CodeInfo[] getOpenCodes() {
        int size = map.size();
        ArrayList<CodeInfo> cis = new ArrayList<ReturnCode.CodeInfo>(size);
        for (int i = 0; i < size; i++) {
            ReturnCode c = map.get(map.keyAt(i));
            if (c.desc == null) continue;// desc为空说明这个是要被隐藏的code
            CodeInfo ci = new CodeInfo(c.code, c.name, c.desc);
            cis.add(ci);
        }
        Collections.sort(cis, new Comparator<CodeInfo>() {

            @Override
            public int compare(CodeInfo o1, CodeInfo o2) {
                return o1.code - o2.code;
            }
        });
        return cis.toArray(EMPTY_CODE_INFO_ARRAY);
    }

    /**
     * 初始化一个对外暴露的ReturnCode
     * 
     * @param name
     * @param desc
     * @param code
     */
    protected ReturnCode(String name, String desc, int code) {
        this.name = name;
        this.desc = desc;
        this.code = code;
        this.shadow = null;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    /**
     * 初始化一个不对外暴露的ReturnCode
     * 
     * @param name
     * @param code
     * @param shadow
     */
    protected ReturnCode(String name, int code, ReturnCode shadow) {
        this.name = name;
        this.desc = null;
        this.code = code;
        this.shadow = shadow;
        if (map.get(code) != null) {
            throw new RuntimeException("ambiguous code definition. " + code);
        }
        map.put(code, this);
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public ReturnCode getShadow() {
        return shadow;
    }

    public static class CodeInfo {
        public int    code;
        public String name;
        public String desc;

        public CodeInfo(int code, String name, String desc) {
            this.code = code;
            this.name = name;
            this.desc = desc;
        }
    }
}
