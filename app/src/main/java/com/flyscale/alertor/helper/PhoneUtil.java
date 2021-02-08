package com.flyscale.alertor.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog.Calls;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.android.internal.telephony.ITelephony;
import com.flyscale.alertor.base.BaseApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bian on 2018/8/25.
 */

public class PhoneUtil {

    private static final String TAG = "PhoneUtil";

    public static final int EMPTY = 1001;
    public static final int NUMBER_CHAR_INVALID = 1002;
    public static final int NUMBER_INVALID = 1003;
    public static final int OK = 0;

    /**
     * 接听电话
     *
     * @param context
     */
    public static void answerCall(Context context) {
        DDLog.d("answerCall()");
        try {
            // 获取getITelephony的方法对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            iTelephony.answerRingingCall();// 挂断电话 权限
            // android.permission.CALL_PHONE
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 挂断电话
     *
     * @param context
     */
    public static void endCall(Context context) {
        DDLog.d("endCall()");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 获取getITelephony的方法对象
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            iTelephony.endCall();// 挂断电话 权限 android.permission.CALL_PHONE
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isRinging(Context context) {
        boolean isRinging = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 获取getITelephony的方法对象
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            isRinging = iTelephony.isRinging();
        } catch (Exception e) {
            e.printStackTrace();
            isRinging = false;
        } finally {
            Log.d(TAG, "isRinging(),isRinging=" + isRinging);
            return isRinging;
        }
    }

    public static boolean isIdle(Context context) {
        boolean isIdle = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 获取getITelephony的方法对象
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            return isIdle = iTelephony.isIdle();
        } catch (Exception e) {
            e.printStackTrace();
            isIdle = false;
        } finally {
            Log.d(TAG, "isIdle(),isIdle=" + isIdle);
            return isIdle;
        }
    }

    public static boolean isOffhook(Context context) {
        boolean isOffhook = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 获取getITelephony的方法对象
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            isOffhook = iTelephony.isOffhook();
        } catch (Exception e) {
            e.printStackTrace();
            isOffhook = false;
        } finally {
            Log.d(TAG, "isOffhook(),isOffhook=" + isOffhook);
            return isOffhook;
        }
    }

    /**
     * 来电静音
     *
     * @param context
     */
    public static void silenceRinger(Context context) {
        DDLog.d("silenceRinger()");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            // 获取getITelephony的方法对象
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);// 设置私有方法可以访问
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);
            iTelephony.silenceRinger();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示拨号界面
     *
     * @param context
     */
    public static void dial(Context context, String number) {
        Log.d(TAG, "dial()");
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);// 执行方法获取ITelephony的对象
            iTelephony.dial(number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拨打电话
     *
     * @param context
     */
    public static void call(Context context, String number) {
        DDLog.i(PhoneUtil.class, "call,number=" + number);
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            @SuppressLint("PrivateApi") Method getITelephonyMethod = telephonyManager.getClass().getDeclaredMethod("getITelephony");
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager);// 执行方法获取ITelephony的对象
            iTelephony.call(context.getPackageName(), number);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回电话状态
     * {@link TelephonyManager#CALL_STATE_RINGING} 1
     * {@link TelephonyManager#CALL_STATE_OFFHOOK}  2
     * {@link TelephonyManager#CALL_STATE_IDLE} 0
     */
    public static int getPhoneState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getCallState();
    }


    public static int checkNum(String num) {
        if (TextUtils.isEmpty(num)) {
            return EMPTY;
        }
        Pattern patern = Pattern.compile("[0-9*#+ABC]*");
        Matcher matcher = patern.matcher(num);
        if (matcher.matches()) {
            return OK;
        }
        return NUMBER_INVALID;
    }

    /**
     * 获取上次拨出电话号码
     */
    public static String getLastOutNumber(Context context) {
        @SuppressLint("MissingPermission") Cursor query = context.getContentResolver().query(Calls.CONTENT_URI, new String[]{"number", "date"},
                "type=2", null, "date desc");
        if (query != null) {
            if (query.moveToFirst()) {
                return query.getString(query.getColumnIndex("number"));
            }
        }
        return null;
    }


    @SuppressLint({"MissingPermission", "HardwareIds"})
    private static String getSerialNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String[] getRamInfo() {
        DDLog.i(PhoneUtil.class, "getRamInfo()");
        String[] meminfo = new String[2];
        try {
            FileReader fileReader = new FileReader("/proc/meminfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            for (int i = 0; i < 2; i++) {
                String line = bufferedReader.readLine();
                //  '\\s'表示空格,回车,换行等空白符,+号表示一个或多个
//                String[] split = line.split("//s+");
                String[] split = line.split(":");
                meminfo[i] = split[1].trim();
                DDLog.i(PhoneUtil.class, "mem=" + split[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return meminfo;
    }

    public static String[] getRamInfo(Context context) {
        DDLog.i(PhoneUtil.class, "getRamInfo");
        String[] meminfo = new String[2];
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        assert am != null;
        am.getMemoryInfo(memoryInfo);
//        meminfo[0] = memoryInfo.totalMem / 1024 + " KB";
//        meminfo[1] = memoryInfo.availMem / 1024 + " KB";
        meminfo[0] = memoryInfo.totalMem + "";
        meminfo[1] = memoryInfo.availMem + "";
        DDLog.i(PhoneUtil.class, "totalMem=" + meminfo[0]);
        DDLog.i(PhoneUtil.class, "availMem=" + meminfo[1]);
        return meminfo;
    }


    /**
     * 获取Sim卡状态
     *
     * @param context
     * @return
     */
    public static String getSimcardState(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        assert tm != null;
        int simState = tm.getSimState();
        DDLog.i(PhoneUtil.class, "getSimcardState(),simState=" + simState);
        return simState + "";
    }

    /**
     * 获取信号强度
     *
     * @return
     */
    public static int getMobileDbm() {
        Context context = BaseApplication.sContext;
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList;

        if (ActivityCompat.checkSelfPermission(BaseApplication.sContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return dbm;
        }

        cellInfoList = tm.getAllCellInfo();
        if (null != cellInfoList) {
            for (CellInfo cellInfo : cellInfoList) {
                if (cellInfo instanceof CellInfoGsm) {
                    CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthGsm.getDbm();
                } else if (cellInfo instanceof CellInfoCdma) {
                    CellSignalStrengthCdma cellSignalStrengthCdma =
                            ((CellInfoCdma) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthCdma.getDbm();
                } else if (cellInfo instanceof CellInfoLte) {
                    CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                    dbm = cellSignalStrengthLte.getDbm();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (cellInfo instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma cellSignalStrengthWcdma =
                                ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthWcdma.getDbm();
                    }
                }
            }
        }
        return dbm;
    }
}
