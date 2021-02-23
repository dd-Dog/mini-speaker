package com.flyscale.alertor.helper;




import android.util.Log;

import com.flyscale.alertor.data.persist.FMLitepalBean;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class FMLitepalUtil extends LitePalSupport {

    public static final ArrayList<Long> FM_SHOW_LIST = new ArrayList<Long>();

    static {
        for (long i = 0x00000100L; i <= 0x0000011FL; i++) {
            FM_SHOW_LIST.add(i);
        }
    }

    /**
     * 判断address是否在所给范围内
     * */
    public static boolean isFmShow(long address){
        for (long i = 0x00000100L; i <= 0x0000011FL; i++) {
            if (address == i){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * 初始化fm信息数据库
     * */
    public static void init(){
        int n = 1;
        for (long i = 0x00000100L; i <= 0x0000011FL; i++) {
                FMLitepalBean litepalBean = new FMLitepalBean();
                litepalBean.setName("FM" + n);
                litepalBean.setStartDate("0");
                litepalBean.setFreq("0.0");
                litepalBean.setStartTime("00:00");
                litepalBean.setEndTime("00:00");
                litepalBean.setVolume("0");
                litepalBean.setIsSetUp("false");
                litepalBean.save();
                n++;
        }
        List<String> isSetUp = FMLitepalUtil.getIsSetUpId();

	if (!isSetUp.isEmpty()){
            for (String a:isSetUp){
                Log.e("fengpj","重新设置已经修改过的行  == " +a);
                int id = Integer.parseInt(a);
                String data = getData(id);
                DateUtil.updataAlarmForFM(id,getWeeklyRecord(data),getFreq(data),
                        getStartFMTime(data),getEndFMTime(data),getVolume(data));
            }
        }
    }

    /**
     * 获取当前修改的行
     * */
    public static int getCorrectLine(long address){
        for (int i=1; i <= 32; i++){
            if (FM_SHOW_LIST.get(i-1) == address){
                return i;
            }
        }
        return 0;
    }

    /**
     * 修改指定行fm信息
     * */
    public static void updateLine(long address,String data){
        FMLitepalBean litepalBean = new FMLitepalBean();
        litepalBean.setFreq(getFreq(data));
        litepalBean.setStartDate(getWeeklyRecord(data));
        litepalBean.setStartTime(getStartFMTime(data));
        litepalBean.setEndTime(getEndFMTime(data));
        litepalBean.setVolume(getVolume(data));
        litepalBean.setIsSetUp("true");
        litepalBean.setData(data);
        litepalBean.updateAll("name = ?","FM" + getCorrectLine(address));
        DateUtil.updataAlarmForFM(getCorrectLine(address),getWeeklyRecord(data),getFreq(data),
                getStartFMTime(data),getEndFMTime(data),getVolume(data));
    }

    /**
     * 判断指定行FM信息是否更新
     * */
    public static boolean isUpdataSuccess(long address,String data){
        int id = getCorrectLine(address);
        String freq = getFreq(data);
        String weeklyRecord = getWeeklyRecord(data);
        String startTime = getStartFMTime(data);
        String endTime = getEndFMTime(data);
        String volume = getVolume(data);
        if(freq.equals(getFreq(id))
        && weeklyRecord.equals(getWeeklyRecord(id))
        && startTime.equals(getStartTime(id))
        && endTime.equals(getEndTime(id))
        && volume.equals(getVolume(id))){
            return true;
        }
        return false;
    }


    /**
     * 获取所需频道
     * */
    public static String getFreq(String data){
        String freq = data.substring(7,14);
        return freq;
    }

    /**
     * 获取开始播放时间
     * */
    public static String getStartFMTime(String data){
        String startTime = data.substring(15,21);
        return startTime;
    }

    /**
     * 获取结束播放时间
     * */
    public static String getEndFMTime(String data){
        String endTime = data.substring(22,28);
        return endTime;
    }

    /**
     * 转化拼接字符串  调整时间表达
     * HH:mm:ss
     * */
    public static String splicingString(String string){
        String hour = string.substring(0,2);
        String min = string.substring(2,4);
        String second = string.substring(4);
        String time = hour + ":" + min + ":" + second;
        return  time;
    }

    /**
     * 获取单周播放信息
     * */
    public static String getWeeklyRecord(String data){
        String weeklyRecord = data.substring(0,3);
        return weeklyRecord;
    }

    /**
     * 获取播放音量
     * */
    public static String getVolume(String data){
        String volume = data.substring(29,30);
        return volume;
    }

    /**
     * 获取数据库中的weeklyrecord
     * */
    public static String getWeeklyRecord(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","startDate").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean weekly:litepalBean){
                return weekly.getStartDate();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的startTime
     * */
    public static String getStartTime(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","startTime").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean startTime:litepalBean){
                return startTime.getStartTime();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的endTime
     * */
    public static String getEndTime(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","endTime").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean endTime:litepalBean){
                return endTime.getEndTime();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的freq
     * */
    public static String getFreq(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","freq").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean freq:litepalBean){
                return freq.getFreq();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的volume
     * */
    public static String getVolume(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","volume").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean volume:litepalBean){
                return volume.getVolume();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的name id
     * */
    public static String getNameId(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","name").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean name:litepalBean){
                return name.getName().substring(2);
            }
        }
        return null;
    }

    /**
     * 获取数据库中的isSetUp
     * */
    public static String getIsSetUp(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","isSetUp").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean name:litepalBean){
                return name.getIsSetUp();
            }
        }
        return null;
    }

    /**
     * 获取isSrtUp 为true的fmid
     * */
    public static List<String> getIsSetUpId(){
        List<FMLitepalBean> litepalBean= LitePal.select("name","isSetUp").where("isSetUp = ?","true").find(FMLitepalBean.class);
        List<String> isSetUpList =new ArrayList<String>();
        if(litepalBean.size() > 0){
            for (FMLitepalBean name:litepalBean){
                isSetUpList.add(name.getName().substring(2));
            }
            return isSetUpList;
        }
        return null;
    }

    /**
     * 获取数据库中的data
     * */
    public static String getData(int fmId){
        List<FMLitepalBean> litepalBean= LitePal.select("name","data").where("name = ?","FM"+fmId).find(FMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (FMLitepalBean data:litepalBean){
                return data.getData();
            }
        }
        return null;
    }
}
