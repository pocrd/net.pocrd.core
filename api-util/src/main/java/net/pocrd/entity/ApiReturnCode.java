package net.pocrd.entity;

public class ApiReturnCode extends AbstractReturnCode {

    public final static int                _C_NO_ASSIGN = Integer.MIN_VALUE;
    public final static AbstractReturnCode NO_ASSIGN    = new ApiReturnCode("未分配返回值", _C_NO_ASSIGN);

    public final static int                _C_SUCCESS = 0;
    public final static AbstractReturnCode SUCCESS    = new ApiReturnCode("成功", _C_SUCCESS);

    public final static int                _C_UNKNOWN_ERROR = -100;
    public final static AbstractReturnCode UNKNOWN_ERROR    = new ApiReturnCode("服务端返回未知错误", _C_UNKNOWN_ERROR);

    /**
     * 内部服务异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_INTERNAL_SERVER_ERROR = -101;
    public final static  AbstractReturnCode INTERNAL_SERVER_ERROR    = new ApiReturnCode(_C_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);

    /**
     * 内部序列化异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_SERIALIZE_FAILED = -102;
    public final static  AbstractReturnCode SERIALIZE_FAILED    = new ApiReturnCode(_C_SERIALIZE_FAILED, UNKNOWN_ERROR);

    /**
     * ip受限, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_IP_DENIED = -103;
    public final static  AbstractReturnCode IP_DENIED    = new ApiReturnCode(_C_IP_DENIED, UNKNOWN_ERROR);

    /**
     * 严重错误, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_FATAL_ERROR = -104;
    public final static  AbstractReturnCode FATAL_ERROR    = new ApiReturnCode(_C_FATAL_ERROR, UNKNOWN_ERROR);

    /**
     * 网络访问失败, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_WEB_ACCESS_FAILED = -105;
    public final static  AbstractReturnCode WEB_ACCESS_FAILED    = new ApiReturnCode(_C_WEB_ACCESS_FAILED, UNKNOWN_ERROR);

    /**
     * security service调用异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_SECURITY_SERVICE_ERROR = -106;
    public final static  AbstractReturnCode SECURITY_SERVICE_ERROR    = new ApiReturnCode(_C_SECURITY_SERVICE_ERROR, UNKNOWN_ERROR);

    /**
     * dubbo服务找不到, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_DUBBO_SERVICE_NOTFOUND_ERROR = -107;
    public final static  AbstractReturnCode DUBBO_SERVICE_NOTFOUND_ERROR    = new ApiReturnCode(_C_DUBBO_SERVICE_NOTFOUND_ERROR, UNKNOWN_ERROR);

    /**
     * dubbo服务调用超时, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_DUBBO_SERVICE_TIMEOUT_ERROR = -108;
    public final static  AbstractReturnCode DUBBO_SERVICE_TIMEOUT_ERROR    = new ApiReturnCode(_C_DUBBO_SERVICE_TIMEOUT_ERROR, UNKNOWN_ERROR);

    /**
     * dubbo服务异常, 对外显示为UNKNOWN_ERROR
     */
    private final static int                _C_DUBBO_SERVICE_ERROR = -109;
    public final static  AbstractReturnCode DUBBO_SERVICE_ERROR    = new ApiReturnCode(_C_DUBBO_SERVICE_ERROR, ApiReturnCode.UNKNOWN_ERROR);

    /**
     * 请求解析异常, mt参数解析失败
     */
    public final static int                _C_UNKNOWN_METHOD = -120;
    public final static AbstractReturnCode UNKNOWN_METHOD    = new ApiReturnCode("mt参数服务端无法识别", _C_UNKNOWN_METHOD);

    /**
     * 请求解析异常, mt参数中接口依赖信息解析失败
     */
    public final static int                _C_UNKNOWN_DEPENDENT_METHOD = -121;
    public final static AbstractReturnCode UNKNOWN_DEPENDENT_METHOD    = new ApiReturnCode(_C_UNKNOWN_DEPENDENT_METHOD, UNKNOWN_METHOD);

    /**
     * 请求解析异常, mt参数中接口依赖层次过多
     */
    public final static int                _C_TOO_MANY_DEPENDENT_LEVEL = -121;
    public final static AbstractReturnCode TOO_MANY_DEPENDENT_LEVEL    = new ApiReturnCode(_C_TOO_MANY_DEPENDENT_LEVEL, UNKNOWN_METHOD);

    public final static int                _C_PARAMETER_ERROR = -140;
    public final static AbstractReturnCode PARAMETER_ERROR    = new ApiReturnCode("参数错误", _C_PARAMETER_ERROR);

    /**
     * 密文参数解密失败
     */
    public final static int                _C_PARAMETER_DECRYPT_ERROR = -141;
    public final static AbstractReturnCode PARAMETER_DECRYPT_ERROR    = new ApiReturnCode(_C_PARAMETER_DECRYPT_ERROR, PARAMETER_ERROR);

    public final static int                _C_ACCESS_DENIED = -160;
    public final static AbstractReturnCode ACCESS_DENIED    = new ApiReturnCode("访问被拒绝", _C_ACCESS_DENIED);

    /**
     * 用户身份验证失败, 对外显示为ACCESS_DENIED
     */
    public final static int                _C_USER_CHECK_FAILED = -161;
    public final static AbstractReturnCode USER_CHECK_FAILED    = new ApiReturnCode(_C_USER_CHECK_FAILED, ACCESS_DENIED);

    /**
     * 访问令牌无法解析,设备信息不足, 对外显示为ACCESS_DENIED
     */
    public final static int                _C_UNKNOW_TOKEN_DENIED = -164;
    public final static AbstractReturnCode UNKNOW_TOKEN_DENIED    = new ApiReturnCode(_C_UNKNOW_TOKEN_DENIED, ACCESS_DENIED);

    /**
     * encryptionOnly接口不接受来自非安全通道的访问, 对外显示为ACCESS_DENIED
     */
    public final static int                _C_UNKNOW_ENCRYPTION_DENIED = -165;
    public final static AbstractReturnCode UNKNOW_ENCRYPTION_DENIED    = new ApiReturnCode(_C_UNKNOW_ENCRYPTION_DENIED, ACCESS_DENIED);

    /**
     * risk manager 返回要阻止相关调用, 对外显示为ACCESS_DENIED
     */
    public final static int                _C_RISK_MANAGER_DENIED = -166;
    public final static AbstractReturnCode RISK_MANAGER_DENIED    = new ApiReturnCode(_C_RISK_MANAGER_DENIED, ACCESS_DENIED);

    /**
     * security level missmatch
     */
    public final static int                _C_SECURITY_LEVEL_MISSMATCH = -167;
    public final static AbstractReturnCode SECURITY_LEVEL_MISSMATCH    = new ApiReturnCode(_C_SECURITY_LEVEL_MISSMATCH, ACCESS_DENIED);

    public final static int                _C_SIGNATURE_ERROR = -180;
    public final static AbstractReturnCode SIGNATURE_ERROR    = new ApiReturnCode("签名错误", _C_SIGNATURE_ERROR);

    public final static int                _C_ILLEGAL_MULTIAPI_ASSEMBLY = -190;
    public final static AbstractReturnCode ILLEGAL_MULTIAPI_ASSEMBLY    = new ApiReturnCode("非法的请求组合", _C_ILLEGAL_MULTIAPI_ASSEMBLY);

    /**
     * RawString返回类型不允许进行多接口同时调用
     */
    public final static int                _C_ILLEGAL_MUTLI_RAWSTRING_RT = -191;
    public final static AbstractReturnCode ILLEGAL_MUTLI_RAWSTRING_RT    = new ApiReturnCode(_C_ILLEGAL_MUTLI_RAWSTRING_RT,
            ILLEGAL_MULTIAPI_ASSEMBLY);

    /**
     * Integrated级别接口不允许进行组合访问
     */
    public final static int                _C_ILLEGAL_MUTLI_INTEGRATED_API_ACCESS = -192;
    public final static AbstractReturnCode ILLEGAL_MUTLI_INTEGRATED_API_ACCESS    = new ApiReturnCode(_C_ILLEGAL_MUTLI_INTEGRATED_API_ACCESS,
            ILLEGAL_MULTIAPI_ASSEMBLY);

    public final static int                _C_REQUEST_PARSE_ERROR = -200;
    public final static AbstractReturnCode REQUEST_PARSE_ERROR    = new ApiReturnCode("请求解析错误", _C_REQUEST_PARSE_ERROR);

    public final static int                _C_API_UPGRADE = -220;
    public final static AbstractReturnCode API_UPGRADE    = new ApiReturnCode("接口已升级", _C_API_UPGRADE);

    public final static int                _C_MOBILE_NOT_REGIST = -250;
    public final static AbstractReturnCode MOBILE_NOT_REGIST    = new ApiReturnCode("手机号未绑定", _C_MOBILE_NOT_REGIST);

    public final static int                _C_DYNAMIC_CODE_ERROR = -260;
    public final static AbstractReturnCode DYNAMIC_CODE_ERROR    = new ApiReturnCode("手机动态密码错误", _C_DYNAMIC_CODE_ERROR);

    public final static int                _C_UPLINK_SMS_NOT_RECEIVED = -270;
    public final static AbstractReturnCode UPLINK_SMS_NOT_RECEIVED    = new ApiReturnCode("上行短信尚未收到", _C_UPLINK_SMS_NOT_RECEIVED);

    public final static int                _C_APPID_NOT_EXIST = -280;
    public final static AbstractReturnCode APPID_NOT_EXIST    = new ApiReturnCode("应用id不存在", _C_APPID_NOT_EXIST);

    public final static int                _C_TOKEN_EXPIRE = -300;
    public final static AbstractReturnCode TOKEN_EXPIRE    = new ApiReturnCode("token已过期", _C_TOKEN_EXPIRE);

    public final static int                _C_NO_TRUSTED_DEVICE = -320;
    public final static AbstractReturnCode NO_TRUSTED_DEVICE    = new ApiReturnCode("不是用户的受信设备", _C_NO_TRUSTED_DEVICE);

    public final static int                _C_NO_ACTIVE_DEVICE = -340;
    public final static AbstractReturnCode NO_ACTIVE_DEVICE    = new ApiReturnCode("不是激活设备(用户在其他地方登录)", _C_NO_ACTIVE_DEVICE);

    public final static int                _C_TOKEN_ERROR = -360;
    public final static AbstractReturnCode TOKEN_ERROR    = new ApiReturnCode("token错误", _C_TOKEN_ERROR);

    public final static int                _C_USER_LOCKED = -370;
    public final static AbstractReturnCode USER_LOCKED    = new ApiReturnCode("用户被锁定", _C_USER_LOCKED);

    public final static int                _C_UPLOAD_FILE_TOO_LARGE = -380;
    public final static AbstractReturnCode UPLOAD_FILE_TOO_LARGE    = new ApiReturnCode("上传文件过大", _C_UPLOAD_FILE_TOO_LARGE);

    public final static int                _C_UPLOAD_FILE_NAME_ERROR = -390;
    public final static AbstractReturnCode UPLOAD_FILE_NAME_ERROR    = new ApiReturnCode("上传文件名错误", _C_UPLOAD_FILE_NAME_ERROR);

    public final static int                _C_ROLE_DENIED = -400;
    public final static AbstractReturnCode ROLE_DENIED    = new ApiReturnCode("当前用户权限不足", _C_ROLE_DENIED);

    protected ApiReturnCode(String desc, int code) {
        super(desc, code);
    }

    protected ApiReturnCode(int code, AbstractReturnCode display) {
        super(code, display);
    }

}
