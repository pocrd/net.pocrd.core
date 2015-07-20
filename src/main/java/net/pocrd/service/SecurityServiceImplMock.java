package net.pocrd.service;

import net.pocrd.entity.CallerInfo;

@Deprecated
public class SecurityServiceImplMock implements SecurityService {

    @Override
    public int getUserDeviceBindingState(Integer appId, Long deviceId, Long userId) {
        return 2;
    }
    @Override
    public boolean isMobileOwner(String mobile, String smsPass) {
        return true;
    }
    @Override
    public CallerInfo getUserInfoBySmsPass(Integer appId, Long deviceId, String mobile, String smsPass) {
        CallerInfo callerInfo = new CallerInfo();
        callerInfo.appid = 2;
        callerInfo.uid = 1234567890909L;
        callerInfo.deviceId = -1000;
        return callerInfo;
    }
    @Override
    public long getLockedEndTime(Long userId) {
        return 1000000000000L;
    }
}
