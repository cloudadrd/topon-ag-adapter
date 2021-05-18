package com.business.support.reallycheck;

import android.content.Context;
import android.text.TextUtils;

import java.net.InetAddress;

public class WireSharkCheck {

    public static ResultData validCheck(Context context) {
        return new ResultData(isInterceptNet(false, true), "");
    }


    public static boolean isInterceptNet(boolean isDebug, boolean isHttps) {
        if (!isDebug) {
            String proxyAddress, portStr;
            int proxyPort;
            String hostKey, portKey;
            if (isHttps) {
                hostKey = "https.proxyHost";
                portKey = "https.proxyPort";
            } else {
                hostKey = "http.proxyHost";
                portKey = "http.proxyPort";
            }

            proxyAddress = System.getProperty(hostKey);
            portStr = System.getProperty(portKey);
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
            if ((!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1)) {
                return internalIpAndDomain(proxyAddress);
            }
        }
        return false;
    }


    private static boolean internalIpAndDomain(String host) {
        try {
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();
            return internalIp(ip);
        } catch (Exception e) {
            return false;
        }
    }


    private static boolean internalIp(String ip) {
        byte[] andre = textToNumericFormatV4(ip);
        assert andre != null;
        return internalIp(andre);
    }


    /**
     * tcp/ip协议中，专门保留了三个IP地址区域作为私有地址，其地址范围如下：
     * <p>
     * 10.0.0.0/8：10.0.0.0～10.255.255.255
     * 172.16.0.0/12：172.16.0.0～172.31.255.255
     * 192.168.0.0/16：192.168.0.0～192.168.255.255
     *
     * @param andre
     * @return
     */
    private static boolean internalIp(byte[] andre) {
        final byte b0 = andre[0];
        final byte b1 = andre[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }
    }

    /**
     * Coming openJdk 7 IPAddressUtil
     *
     * @param src
     * @return
     */
    private static byte[] textToNumericFormatV4(String src) {
        byte[] res = new byte[4];

        long tmpValue = 0;
        int currByte = 0;

        int len = src.length();
        if (len == 0 || len > 15) {
            return null;
        }
        /*
         * When only one part is given, the value is stored directly in
         * the network address without any byte rearrangement.
         *
         * When a two part address is supplied, the last part is
         * interpreted as a 24-bit quantity and placed in the right
         * most three bytes of the network address. This makes the
         * two part address format convenient for specifying Class A
         * network addresses as net.host.
         *
         * When a three part address is specified, the last part is
         * interpreted as a 16-bit quantity and placed in the right
         * most two bytes of the network address. This makes the
         * three part address format convenient for specifying
         * Class B net- work addresses as 128.net.host.
         *
         * When four parts are specified, each is interpreted as a
         * byte of data and assigned, from left to right, to the
         * four bytes of an IPv4 address.
         *
         * We determine and parse the leading parts, if any, as single
         * byte values in one pass directly into the resulting byte[],
         * then the remainder is treated as a 8-to-32-bit entity and
         * translated into the remaining bytes in the array.
         */
        for (int i = 0; i < len; i++) {
            char c = src.charAt(i);
            if (c == '.') {
                if (tmpValue < 0 || tmpValue > 0xff || currByte == 3) {
                    return null;
                }
                res[currByte++] = (byte) (tmpValue & 0xff);
                tmpValue = 0;
            } else {
                int digit = Character.digit(c, 10);
                if (digit < 0) {
                    return null;
                }
                tmpValue *= 10;
                tmpValue += digit;
            }
        }
        if (tmpValue < 0 || tmpValue >= (1L << ((4 - currByte) * 8))) {
            return null;
        }
        switch (currByte) {
            case 0:
                res[0] = (byte) ((tmpValue >> 24) & 0xff);
            case 1:
                res[1] = (byte) ((tmpValue >> 16) & 0xff);
            case 2:
                res[2] = (byte) ((tmpValue >> 8) & 0xff);
            case 3:
                res[3] = (byte) ((tmpValue) & 0xff);
        }
        return res;
    }
}
