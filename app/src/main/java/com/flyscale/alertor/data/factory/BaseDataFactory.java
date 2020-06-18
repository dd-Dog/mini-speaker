package com.flyscale.alertor.data.factory;

import android.text.TextUtils;

import com.flyscale.alertor.R;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.down.DAddDeleteWhiteList;
import com.flyscale.alertor.data.down.DAlarm;
import com.flyscale.alertor.data.down.DChangeAlarmNumber;
import com.flyscale.alertor.data.down.DChangeCallNumberHZ;
import com.flyscale.alertor.data.down.DChangeClientCa;
import com.flyscale.alertor.data.down.DChangeHeart;
import com.flyscale.alertor.data.down.DChangeIP;
import com.flyscale.alertor.data.down.DChangeTestKeyNumber;
import com.flyscale.alertor.data.down.DClientAlarm;
import com.flyscale.alertor.data.down.DDeleteVoice;
import com.flyscale.alertor.data.down.DDoorAlarm;
import com.flyscale.alertor.data.down.DGasAlarm;
import com.flyscale.alertor.data.down.DHeart;
import com.flyscale.alertor.data.down.DRing;
import com.flyscale.alertor.data.down.DSmokeAlarm;
import com.flyscale.alertor.data.down.DUpdateNumberRing;
import com.flyscale.alertor.data.down.DUpdateRecord;
import com.flyscale.alertor.data.down.DUpdateVersion;
import com.flyscale.alertor.data.down.DVoice;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 16:35
 * @DESCRIPTION 暂无
 */
public class BaseDataFactory {

    /**
     * 下行报文的类型
     * @param result
     * @return
     */
    public static int parseType(String result){
        result = result.replace(BaseData.FLAG_START,"");
        result = result.replace(BaseData.FLAG_END,"");
        String[] array = TextUtils.split(result,BaseData.FLAG_SPLIT);
        return Integer.parseInt(array[1]);
    }

    /**
     * 获取回执数据的实例
     * @param result
     * @return
     */
    public static BaseData getDataInstance(String result){
        int type = parseType(result);
        return getDataInstance(type);
    }

    /**
     * 根据类型获取回执数据实例
     * @param type
     * @return
     */
    public static BaseData getDataInstance(int type){
        switch (type){
            case BaseData.TYPE_HEART_D:
                return new DHeart();
            case BaseData.TYPE_ALARM_D:
                return new DAlarm();
            case BaseData.TYPE_ADD_OR_DELETE_WHITE_LIST_D:
                return new DAddDeleteWhiteList();
            case BaseData.TYPE_CHANGE_ALARM_NUMBER_D:
                return new DChangeAlarmNumber();
            case BaseData.TYPE_CHANGE_CALL_HZ_D:
                return new DChangeCallNumberHZ();
            case BaseData.TYPE_CHANGE_CLIENT_CA_D:
                return new DChangeClientCa();
            case BaseData.TYPE_CHANGE_HEART_D:
                return new DChangeHeart();
            case BaseData.TYPE_CHANGE_IP_D:
                return new DChangeIP();
            case BaseData.TYPE_CHANGE_TEST_KEY_NUMBER_D:
                return new DChangeTestKeyNumber();
            case BaseData.TYPE_CLIENT_ALARM_D:
                return new DClientAlarm();
            case BaseData.TYPE_DELETE_VOICE_D:
                return new DDeleteVoice();
            case BaseData.TYPE_DOOR_ALARM_D:
                return new DDoorAlarm();
            case BaseData.TYPE_GAS_ALARM_D:
                return new DGasAlarm();
            case BaseData.TYPE_RING_D:
                return new DRing();
            case BaseData.TYPE_SMOKE_ALARM_D:
                return new DSmokeAlarm();
            case BaseData.TYPE_UPDATE_NUMBER_RING_D:
                return new DUpdateNumberRing();
            case BaseData.TYPE_CLIENT_UPDATE_RECORD_D:
                return new DUpdateRecord();
            case BaseData.TYPE_UPDATE_VERSION_D:
                return new DUpdateVersion();
            case BaseData.TYPE_VOICE_D:
                return new DVoice();

        }
        return null;
    }
}
