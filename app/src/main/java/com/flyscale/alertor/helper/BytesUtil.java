package com.flyscale.alertor.helper;

import java.nio.ByteBuffer;

public class BytesUtil {

    /**
     * long转字节数组
     * @param value
     * @return
     */
    public static byte[] longToBytes(long value) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(value).array();
    }

    /**
     * int转字节数组
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value) {
        return ByteBuffer.allocate(Integer.SIZE / Byte.SIZE).putInt(value).array();
    }

    /**
     * 10进制转16进制
     * @param num
     * @return
     */
    public static String toHex(long num) {
        String hexString = Long.toHexString(num);
        return hexString;
    }

    /**
     * Long转十六进制字符串的字节数组
     * @param num
     * @return 返回4字节长度的字节数组
     */
    public static byte[] longToHexBytes(long num){
        String hexString = toHex(num);
        return hexString.getBytes();
    }

    public static byte[] intToHexBytesRevert(int num){
        String hexString = Integer.toHexString(num);
        int high = Integer.parseInt(hexString.substring(0, 2), 16);
        int low = Integer.parseInt(hexString.substring(2,4), 16);
        return new byte[]{(byte) low, (byte) high};
    }

    /**
     * 字节数组转long
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

}
