package net.pocrd.service;

import net.pocrd.entity.CallerInfo;

/**
 * 仅暴露给网关使用的dubbo服务
 */
public interface SecurityService {

    /**
     * @return 0:未进行用户设备检测 1:是用户的受信设备 2:处于激活状态  -1:不是用户的受信设备 -2:用户被锁定 -3:触发风控，用户被锁定
     */
    int getUserDeviceBindingState(Integer appId, Long deviceId, Long userId);

    /**
     * 验证mobile owner
     */
    boolean isMobileOwner(String mobile, String smsPass);

    CallerInfo getUserInfoBySmsPass(Integer appId, Long deviceId, String mobile, String smsPass);

    /**
     * 获取锁定截止时间
     */
    long getLockedEndTime(Long userId);
}