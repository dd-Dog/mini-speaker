package com.flyscale.alertor.led;

import android.flyscale.FlyscaleManager;

/**
 * @author 高鹤泉
 * @TIME 2020/6/17 11:56
 * @DESCRIPTION 暂无
 */
public interface Constant {
    int CHARGE_LED = FlyscaleManager.CHARGE_LED;//充电指示灯
    int SIGNAL_LED = FlyscaleManager.SIGNAL_LED;//信号指示灯
    int STATE_LED = FlyscaleManager.STATE_LED;//状态指示灯
    int ALARM_LED = FlyscaleManager.ALARM_LED;//报警灯

    //灯的亮度值 越大越亮
    int LED_COLOR_LEVEL_MAX = 255;
    int LED_COLOR_LEVEL_MIN = 0;
}
