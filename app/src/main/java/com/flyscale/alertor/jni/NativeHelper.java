package com.flyscale.alertor.jni;

public class NativeHelper {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String stringFromJNI();

    public static native byte[] desEncrypt(byte[] key, byte[] plainText);

    public static native byte[] desDecrypt(byte[] key, byte[] encryptedData);
}
