package net.pocrd.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MiscUtil {
    public static final String X_FORWARDED_FOR      = "x-forwarded-for";
    public static final String HTTP_X_FORWARDED_FOR = "http-x-forwarded-for";
    public static final String REMOTE_ADDR          = "remote-addr";

    public static String getLocalIP() {
        Enumeration<NetworkInterface> interfaces = null;
        StringBuilder sb = new StringBuilder(20);
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if ("lo".equals(i.getName())) {
                    continue;
                }
                Enumeration<InetAddress> ips = i.getInetAddresses();
                while (ips.hasMoreElements()) {
                    String ip = ips.nextElement().getHostAddress();
                    if (ip != null && !ip.contains(":")) {
                        sb.append(ip);
                        sb.append(";");
                    }
                }
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        } catch (Exception e) {
            // do nothing
        }
        return "0";
    }

    public static String getClientIP(HttpServletRequest request) {
        String ip;
        ip = request.getHeader(X_FORWARDED_FOR);
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
            if (ip == null || ip.length() == 0) {
                ip = request.getHeader(REMOTE_ADDR);
                if (ip == null || ip.length() == 0) {
                    ip = request.getRemoteAddr();
                }
            }
        }
        return ip;
    }
}
