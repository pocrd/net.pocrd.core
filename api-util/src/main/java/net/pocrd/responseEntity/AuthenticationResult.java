package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

/**
 * Created by rendong on 2018/4/10.
 */
@Description("认证结果")
public final class AuthenticationResult {

    @Description("授权访问的用户id")
    public long authorizedUserId;

    @Description("授权访问的接口列表")
    public String[] apis;
}
