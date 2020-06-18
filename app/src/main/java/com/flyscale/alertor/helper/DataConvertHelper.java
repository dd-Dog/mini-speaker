package com.flyscale.alertor.helper;

import android.text.TextUtils;

/**
 * @author 高鹤泉
 * @TIME 2020/6/16 13:16
 * @DESCRIPTION 数据转换 各种进制转化
 */
public class DataConvertHelper {

    public static final String S_HEX_STRING_MATCH = "0123456789ABCDEF";

    /**
     * byte[]转十六进制
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String tmp = Integer.toHexString(bytes[i] & 0xFF);
            hex.append((tmp.length() == 1) ? "0" + tmp : tmp);
        }
        return hex.toString().trim();
    }


    /**
     * 16进制转byte[]
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(String hex){
        if(TextUtils.isEmpty(hex)){
            return null;
        }
        hex = hex.toUpperCase();
        int length = hex.length() / 2;
        char[] hexChars = hex.toCharArray();
        byte[] bytes = new byte[length];
        for(int i=0;i<length;i++){
            int position = i *2;
            bytes[i] = (byte) (hexCharToByte(hexChars[position]) << 4
                                    | hexCharToByte(hexChars[position +1]));
        }
        return bytes;
    }

    /**
     * 16进制char转byte
     * @param c
     * @return
     */
    private static byte hexCharToByte(char c) {
        return (byte) S_HEX_STRING_MATCH.indexOf(c);
    }
}
