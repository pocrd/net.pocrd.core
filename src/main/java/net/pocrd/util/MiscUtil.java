package net.pocrd.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class MiscUtil {
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
        ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("http-x-forwarded-for");
            if (ip == null || ip.length() == 0) {
                ip = request.getHeader("remote-addr");
                if (ip == null || ip.length() == 0) {
                    ip = request.getRemoteAddr();
                }
            }
        }
        return ip;
    }
}
