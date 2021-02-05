package com.flyscale.alertor.jni;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.DDLog;

public class NativeHelper {
    static {
        System.loadLibrary("fly_lib");
    }

    public static native String stringFromJNI();

    public static native byte[] desEncrypt(byte[] key, byte[] plainText);

    public static native byte[] desDecrypt(byte[] key, byte[] encryptedData);

    public static native byte[] desEncrypt(byte[] plainText);

    public static native byte[] desDecrypt(byte[] encryptedData);

    public static byte[] encrypt(byte[] plainText){
        if (PersistConfig.findConfig().isLogin()){
//            DDLog.i("登录状态");
            return desEncrypt(PersistConfig.findConfig().getRandomKey().getBytes(), plainText);
        }else {
//            DDLog.i("未登录状态");
            return desEncrypt(plainText);
        }
    }

    public static byte[] decrypt(byte[] encryptedData){
        if (PersistConfig.findConfig().isLogin()){
//            DDLog.i("登录状态");
            return desDecrypt(PersistConfig.findConfig().getRandomKey().getBytes(), encryptedData);
        }else {
//            DDLog.i("未登录状态");
            return desDecrypt(encryptedData);
        }
    }
}
