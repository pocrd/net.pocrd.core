package net.pocrd.define;

import net.pocrd.responseEntity.AuthenticationResult;

/**
 * Created by rendong on 2018/4/10.
 */
public interface AuthenticationService {
    String AuthMethodSignature = "authenticate(JLjava/lang/String;Ljava/lang/String;)Lnet/pocrd/responseEntity/AuthenticationResult;";

    /**
     * 业务系统通过实现该接口获得授权用户以指定用户身份调用主站接口的能力
     * 例如: 客服系统验证当前用户是客服人员后可以授权其以任意指定用户身份访问订单查询, 用户信息查询等接口。
     *
     * @param userid   当前访问用户id, 必须使用ApiAutowired标注该参数
     * @param authType 业务系统定义一个授权类型枚举, 并将其标注与ApiParameter标识中, 使用此参数决定授权接口列表。
     * @param authInfo 授权信息, 业务系统自行定义业务相关的授权信息, 其中至少包含授权目标用户id。
     *
     * @return 返回的授权结果列表中api数量不能超过10个。
     */
    AuthenticationResult authenticate(long userid, String authType, String authInfo);
}
