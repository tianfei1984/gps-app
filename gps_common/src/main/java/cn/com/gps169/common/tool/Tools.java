package cn.com.gps169.common.tool;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 工具类
 * 
 */
public class Tools {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tools.class);

    @SuppressWarnings("rawtypes")
    public static String getHostAddress() {
        String address = "Unknown host";

        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address && !"127.0.0.1".equals(ip.getHostAddress())) {
                        address = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.warn("获取本机Ip地址时发生异常!", e);
        }

        return address;
    }

    /**
     * 字符串是否全为数字
     * 
     * @param str
     * @return
     */
    public static boolean isDigit(String str) {

        if (str == null) {
            return false;
        }

        if (str.length() < 1) {
            return false;
        }
        return Pattern.matches("^[0-9]+$", str);
    }
}
