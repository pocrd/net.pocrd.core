package net.pocrd.define;

/**
 * Created by rendong on 16/6/27.
 */
public enum AutowireableParameter {
    appid,
    deviceid,
    userid,
    subSystemRole,
    subSystemMainId,
    userAgent,
    cookies,
    headers, // TODO 支持 header 注入
    businessId,
    postBody,
    channel,
    thirdPartyId,
    versionCode,
    referer,
    host,
    token,
    secretToken,
    clientIP,
    serviceInjection,
}
