package net.pocrd.define;

import net.pocrd.annotation.Description;
import net.pocrd.core.ApiManager;
import net.pocrd.entity.CompileConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;

@Description("api通用参数列表")
public final class CommonParameter {
    @Description("format 返回值格式,取值为枚举SerializeType中的定义,取值范围JSON/XML")
    public static final String format = "_ft";

    @Description("location 用于返回信息国际化,目前尚不支持国际化.")
    public static final String location = "_lo";

    @Description("user token 代表访问者身份,完成用户登入流程后获取")
    public static final String token = "_tk";

    @Description("user secret token 只存放于web/h5站点的secret cookie中，用于在不同domain间传递csrfToken")
    public static final String stoken = "_stk";

    @Description("method 请求的资源名")
    public static final String method = "_mt";

    @Description("signature 参数字符串签名")
    public static final String signature = "_sig";

    @Description("application id 应用编号")
    public static final String applicationId = "_aid";

    @Description("business id 业务流水号, 用于做幂等判断, 风控等")
    public static final String businessId = "_bid";

    @Description("call id 客户端调用编号")
    public static final String callId = "_cid";

    @Description("device id 设备标示符")
    public static final String deviceId = "_did";

    @Description("device id 设备标示符, 存储在cookie中的名字")
    public static final String cookieDeviceId = "__da";

    @Description("user id 用户标示符")
    public static final String userId = "_uid";

    @Description("version code 客户端数字版本号.")
    public static final String versionCode = "_vc";

    @Description("signature method 签名算法 hmac,md5,sha1,rsa,ecc")
    public static final String signatureMethod = "_sm";

    @Description("jsonp callback名")
    public static final String jsonpCallback = "_cb";

    @Description("第三方集成的身份标识(第三方集成情景下使用)")
    public static final String thirdPartyId = "_tpid";

    @Description("客户端应用安装渠道")
    public static final String channel = "_ch";

    private static final HashSet<String> names = new HashSet<String>();

    public static boolean contains(String name) {
        return names.contains(name);
    }

    static {
        if (CompileConfig.isDebug) {
            try {
                Field[] fs = CommonParameter.class.getDeclaredFields();
                if (fs != null) {
                    for (Field f : fs) {
                        if (ApiManager.isConstField(f) && f.getType() == String.class && Modifier.isPublic(f.getModifiers())) {
                            String name = (String)f.get(null);
                            if (!name.startsWith("_")) {
                                throw new RuntimeException("Common parameter name should start with '_'. error name:" + name);
                            }
                            names.add(name);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("load names failed.", e);
            }
        }
    }
}
