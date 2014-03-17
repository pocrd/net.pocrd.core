package net.pocrd.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.pocrd.util.SparseArray;

/**
 * 定义通用返回值，所有通用返回值都小于0表示所有业务接口都有可能返回该值。 约定:所有通用返回值code都以‘_C_’开头 约定:所有业务返回值code都以‘C_’开头 约定:所有不直接返回的返回值(用于日志记录/分析)都声明为private成员，且需要定义一个给客户端处理的替代值
 * 
 * @author rendong
 */
public class ReturnCode {
    private static SparseArray<ReturnCode> map                               = new SparseArray<ReturnCode>(100);

    public final static int                _C_NO_ASSIGN                      = Integer.MIN_VALUE;
    public final static ReturnCode         NO_ASSIGN                         = new ReturnCode("NO_ASSIGN", "未分配返回值", _C_NO_ASSIGN);

    public final static int                _C_UNKNOWN_ERROR                  = -100;
    public final static ReturnCode         UNKNOWN_ERROR                     = new ReturnCode("UNKNOWN_ERROR", "服务端返回未知错误", _C_UNKNOWN_ERROR);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int               _C_INTERNAL_SERVER_ERROR          = -101;
    public final static ReturnCode         INTERNAL_SERVER_ERROR             = new ReturnCode("INTERNAL_SERVER_ERROR", _C_INTERNAL_SERVER_ERROR,
                                                                                     UNKNOWN_ERROR);

    /**
     * 内部序列化异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int               _C_SERIALIZE_FAILED               = -102;
    public final static ReturnCode         SERIALIZE_FAILED                  = new ReturnCode("SERIALIZE_FAILED", _C_SERIALIZE_FAILED, UNKNOWN_ERROR);

    /**
     * ip受限, 对外显示为UNKNOWN_ERROR
     */
    private final static int               _C_IP_DENIED                      = -103;
    public final static ReturnCode         IP_DENIED                         = new ReturnCode("IP_DENIED", _C_IP_DENIED, UNKNOWN_ERROR);

    /**
     * 严重错误, 对外显示为UNKNOWN_ERROR
     */
    private final static int               _C_FATAL_ERROR                    = -104;
    public final static ReturnCode         FATAL_ERROR                       = new ReturnCode("FATAL_ERROR", _C_FATAL_ERROR, UNKNOWN_ERROR);

    /**
     * 网络访问失败, 对外显示为UNKNOWN_ERROR
     */
    private final static int               _C_WEB_ACCESS_FAILED              = -105;
    public final static ReturnCode         WEB_ACCESS_FAILED                 = new ReturnCode("WEB_ACCESS_FAILED", _C_WEB_ACCESS_FAILED,
                                                                                     UNKNOWN_ERROR);

    public final static int                _C_UNKNOWN_METHOD                 = -120;
    public final static ReturnCode         UNKNOWN_METHOD                    = new ReturnCode("UNKNOWN_METHOD", "mt参数服务端无法识别", _C_UNKNOWN_METHOD);

    public final static int                _C_PARAMETER_ERROR                = -140;
    public final static ReturnCode         PARAMETER_ERROR                   = new ReturnCode("PARAMETER_ERROR", "参数错误", _C_PARAMETER_ERROR);

    public final static int                _C_ACCESS_DENIED                  = -160;
    public final static ReturnCode         ACCESS_DENIED                     = new ReturnCode("ACCESS_DENIED", "访问被拒绝", _C_ACCESS_DENIED);

    /**
     * token验证失败, 对外显示为ACCESS_DENIED
     */
    private final static int               _C_ACCESS_DENIED_TOKEN_ERROR      = -161;
    public final static ReturnCode         ACCESS_DENIED_TOKEN_ERROR         = new ReturnCode("ACCESS_DENIED_TOKEN_ERROR",
                                                                                     _C_ACCESS_DENIED_TOKEN_ERROR, ACCESS_DENIED);

    /**
     * 未达到接口所需安全级别, 对外显示为ACCESS_DENIED
     */
    private final static int               _C_ACCESS_DENIED_UNMATCH_SECURITY = -162;
    public final static ReturnCode         ACCESS_DENIED_UNMATCH_SECURITY    = new ReturnCode("ACCESS_DENIED_UNMATCH_SECURITY",
                                                                                     _C_ACCESS_DENIED_UNMATCH_SECURITY, ACCESS_DENIED);

    /**
     * device id验证失败, 对外显示为ACCESS_DENIED
     */
    private final static int               _C_ACCESS_DENIED_DEVICEID_ERROR   = -163;
    public final static ReturnCode         ACCESS_DENIED_DEVICEID_ERROR      = new ReturnCode("ACCESS_DENIED_DEVICEID_ERROR",
                                                                                     _C_ACCESS_DENIED_DEVICEID_ERROR, ACCESS_DENIED);

    public final static int                _C_SIGNATURE_ERROR                = -180;
    public final static ReturnCode         SIGNATURE_ERROR                   = new ReturnCode("SIGNATURE_ERROR", "签名错误", _C_SIGNATURE_ERROR);

    public final static int                _C_REQUEST_PARSE_ERROR            = -200;
    public final static ReturnCode         REQUEST_PARSE_ERROR               = new ReturnCode("REQUEST_PARSE_ERROR", "请求解析错误", _C_REQUEST_PARSE_ERROR);

    public final static int                _C_API_UPGRADE                    = -220;
    public final static ReturnCode         API_UPGRADE                       = new ReturnCode("API_UPGRADE", "接口已升级", _C_API_UPGRADE);

    public final static int                C_SUCCESS                         = 0;
    public final static ReturnCode         SUCCESS                           = new ReturnCode("SUCCESS", "成功", C_SUCCESS);

    private final String                   name;
    private final String                   desc;
    private final int                      code;
    private final ReturnCode               shadow;
    private final static ReturnCode[]      empty                             = new ReturnCode[0];

    public static ReturnCode findCode(int c) {
        return map.get(c);
    }

    public static ReturnCode[] getOpenCodes() {
        int size = map.size();
        ArrayList<ReturnCode> cis = new ArrayList<ReturnCode>(size);
        for (int i = 0; i < size; i++) {
            ReturnCode c = map.get(map.keyAt(i));
            if (c.desc == null) continue;// desc为空说明这个是要被隐藏的code
            cis.add(c);
        }
        Collections.sort(cis, new Comparator<ReturnCode>() {

            @Override
            public int compare(ReturnCode o1, ReturnCode o2) {
                return o1.code - o2.code;
            }
        });
        return cis.toArray(empty);
    }

    /**
     * 初始化一个对外暴露的ReturnCode(用于客户端异常处理)
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
     * 初始化一个不对外暴露的ReturnCode(仅用于服务端数据分析)
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
}
