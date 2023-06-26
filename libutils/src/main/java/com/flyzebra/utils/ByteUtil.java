package com.flyzebra.utils;

/**
 * Author: FlyZebra
 * Created by flyzebra on 18-4-9-下午3:11.
 */

public class ByteUtil {
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null || bytes.length == 1) {
            return null;
        }
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < bytes.length; i++) {
            String hv = Integer.toHexString(bytes[i] & 0xFF);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            sb.append(":");
        }
        return sb.toString().toUpperCase();
    }

    public static String bytes2HexString(byte[] bytes, int length) {
        if (bytes == null || length < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        length = Math.min(length, bytes.length);
        for (int i = 0; i < length; i++) {
            String hv = Integer.toHexString(bytes[i] & 0xFF);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            sb.append(":");
        }
        return sb.toString().toUpperCase();
    }

    public static String bytes2HexString(byte[] bytes, int start, int length) {
        if (bytes == null || length < 1 || bytes.length < (start + length)) {
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        length = Math.min(start + length, bytes.length);
        for (int i = start; i < length; i++) {
            String hv = Integer.toHexString(bytes[i] & 0xFF);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
            sb.append(":");
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexString2Bytes(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static short bytes2Short(byte[] bytes, int offset, boolean littleEndian) {
        short value = 0;
        for (int count = 0; count < 2; ++count) {
            int shift = (littleEndian ? count : (1 - count)) << 3;
            value |= ((short) 0xff << shift) & ((short) bytes[offset + count] << shift);
        }
        return value;
    }

    public static void shortToBytes(short value, byte[] bytes, int offset, boolean littleEndian) {
        for (int count = 0; count < 2; ++count) {
            int shift = (littleEndian ? count : (1 - count)) << 3;
            bytes[count + offset] = (byte) (value >> shift & 0xFF);
        }
    }

    public static int bytes2Int(byte[] bytes, int offset, boolean littleEndian) {
        int value = 0;
        for (int count = 0; count < 4; ++count) {
            int shift = (littleEndian ? count : (3 - count)) << 3;
            value |= ((int) 0xff << shift) & ((int) bytes[offset + count] << shift);
        }
        return value;
    }

    public static void intToBytes(int value, byte[] bytes, int offset, boolean littleEndian) {
        for (int count = 0; count < 4; ++count) {
            int shift = (littleEndian ? count : (3 - count)) << 3;
            bytes[count + offset] = (byte) (value >> shift & 0xFF);
        }
    }

    public static long bytes2Long(byte[] bytes, int offset, boolean littleEndian) {
        long value = 0;
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count : (7 - count)) << 3;
            value |= ((long) 0xff << shift) & ((long) bytes[offset + count] << shift);
        }
        return value;
    }

    public static void longToBytes(long value, byte[] bytes, int offset, boolean littleEndian) {
        for (int count = 0; count < 8; ++count) {
            int shift = (littleEndian ? count : (7 - count)) << 3;
            bytes[count + offset] = (byte) (value >> shift & 0xFF);
        }
    }

    public static long sysIdToInt64(String stid) {
        long tid = 0;
        byte[] bytes = stid.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            long ret = 0;
            byte x = (byte) (bytes[i] & 0xFF);
            if (x >= '0' && x <= '9') {
                ret = x - '0';
            }
            else if (x >= 'A' && x <= 'Z') {
                ret = x - 'A' + 10;
            }
            else if (x >= 'a' && x <= 'z') {
                ret = x - 'a' + 10;
            }
            else {
                continue;
            }
            long pow = 1;
            int y = 9 - i;
            for (int j = 1; j < y; j++) {
                pow = pow * 36;
            }
            tid += ret * pow;
        }
        return tid;
    }
}
