package com.flyscale.alertor.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.flyscale.alertor.base.BaseApplication;

/**
 * @author 高鹤泉
 * @TIME 2020/6/16 15:23
 * @DESCRIPTION 持久化数据
 */
public class PersistDataHelper {

    static final String SP_TABLE_NAME = "SP_TABLE_NAME";
    static final String SP_SOCKET_IP_KEY = "SP_SOCKET_IP_KEY";
    static final String SP_SOCKET_PORT_KEY = "SP_SOCKET_PORT_KEY";
    static final String SP_ALARM_NUMBER = "SP_ALARM_NUMBER";
    static final String SP_RECEIVE_ALARM_NUMBER = "SP_RECEIVE_ALARM_NUMBER";


    private static SharedPreferences getSp(){
        return BaseApplication.sContext.getSharedPreferences(SP_TABLE_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor(){
        return getSp().edit();
    }

    /**
     * 获取报警电话
     * @return
     */
    public static String getAlarmNumber(){
        SharedPreferences sharedPreferences = getSp();
        String number = sharedPreferences.getString(SP_ALARM_NUMBER,"09901686119");
        return number;
    }

    /**
     * 存储报警电话
     * @param number
     */
    public static void saveAlarmNumber(String number){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(SP_ALARM_NUMBER,number);
        editor.commit();
    }

    /**
     * 获取接警电话
     * @return
     */
    public static String getReceiveAlarmNumber(){
        SharedPreferences sharedPreferences = getSp();
        String number = sharedPreferences.getString(SP_RECEIVE_ALARM_NUMBER,"09941183111");
        return number;
    }

    /**
     * 保存接警电话
     * @param number
     */
    public static void saveReceiveAlarmNumber(String number){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(SP_RECEIVE_ALARM_NUMBER,number);
        editor.commit();
    }

    /**
     * 获取socketIp
     * @return
     */
    public static String getSocketIp(){
        SharedPreferences sharedPreferences = getSp();
        String ip = sharedPreferences.getString(SP_SOCKET_IP_KEY,"202.100.190.14");
        return ip;
    }

    /**
     * 存储 socketIP
     * @param ip
     */
    public static void saveSocketIp(String ip){
        SharedPreferences.Editor editor = getEditor();
        editor.putString(SP_SOCKET_IP_KEY,ip);
        editor.commit();
    }

    /**
     * 获取socket端口
     * @return
     */
    public static int getSocketPort(){
        SharedPreferences sharedPreferences = getSp();
        int port = sharedPreferences.getInt(SP_SOCKET_PORT_KEY,9988);
        return port;
    }

    /**
     * 存储socketPort
     * @param port
     */
    public static void saveSocketPort(int port){
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(SP_SOCKET_PORT_KEY,port);
        editor.commit();
    }
}
