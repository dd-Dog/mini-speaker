package com.flyscale.alertor.helper;

import android.content.Context;
import android.content.Intent;

public class FMUtil {

    public static float freq = 0f;
    public static boolean isFmOn = false;
    public static boolean isMuted = false;
    public static String freqList = "";
    public static int count = 0;

    public FMUtil() {
    }


    public static void startFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_START");
        context.sendBroadcast(intent);
    }

    public static void pauseFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_PAUSE");
        context.sendBroadcast(intent);
    }

    public static void searchFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_SEARCH");
        context.sendBroadcast(intent);
    }

    public static void stopFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_STOP");
        context.sendBroadcast(intent);
    }

    public static void adjustFM(Context context,float freq){
        Intent intent = new Intent("com.android.flyscale.FM_ADJUST");
        intent.putExtra("freq",freq);
        context.sendBroadcast(intent);
    }

    public static void informationFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_INFORMATION");
        context.sendBroadcast(intent);
    }

    public static float getFreq() {
        return freq;
    }

    public static void setFreq(float freq) {
        FMUtil.freq = freq;
    }

    public static boolean isIsFmOn() {
        return isFmOn;
    }

    public static void setIsFmOn(boolean isFmOn) {
        FMUtil.isFmOn = isFmOn;
    }

    public static boolean isIsMuted() {
        return isMuted;
    }

    public static void setIsMuted(boolean isMuted) {
        FMUtil.isMuted = isMuted;
    }

    public static String getFreqList() {
        return freqList;
    }

    public static void setFreqList(String freqList) {
        FMUtil.freqList = freqList;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        FMUtil.count = count;
    }

    public static void setFMStatus(boolean isFmOn,boolean isMuted,String freqList,float freq,int count){
        FMUtil.isFmOn = isFmOn;
        FMUtil.isMuted = isMuted;
        FMUtil.freqList = freqList;
        FMUtil.freq = freq;
        FMUtil.count = count;
    }


}
