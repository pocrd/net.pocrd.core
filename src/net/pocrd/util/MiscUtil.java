package net.pocrd.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MiscUtil {
    public static String getLocalIP() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                Enumeration<InetAddress> ips = i.getInetAddresses();
                while (ips.hasMoreElements()) {
                    String ip = ips.nextElement().getHostAddress();
                    return ip;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return "0";
    }
}
