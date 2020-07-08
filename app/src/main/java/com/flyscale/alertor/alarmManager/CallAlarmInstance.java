package com.flyscale.alertor.alarmManager;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:11
 * @DESCRIPTION 暂无
 */
@Deprecated
public class CallAlarmInstance {
    private static final CallAlarmInstance ourInstance = new CallAlarmInstance();

    public static CallAlarmInstance getInstance() {
        return ourInstance;
    }

    private CallAlarmInstance() {
    }
}
