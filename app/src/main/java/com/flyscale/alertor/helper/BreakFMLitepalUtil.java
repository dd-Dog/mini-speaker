package com.flyscale.alertor.helper;

import android.util.Log;

import com.flyscale.alertor.data.persist.BreakFMLitepalBean;
import com.flyscale.alertor.data.persist.FMLitepalBean;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class BreakFMLitepalUtil extends LitePalSupport {

    /*7.3.12设置FM插播广播节目*/
    public static final ArrayList<Long> BREAKING_FM_SHOW_LIST = new ArrayList<Long>();
    static {
        for (long i = 0x00000180L; i <= 0x0000019FL; i++) {
            BREAKING_FM_SHOW_LIST.add(i);
        }
    }

    /**
     * 判断address是否在所给范围内
     * */
    public static boolean isFmShow(long address){
        for (long i = 0x00000180L; i <= 0x0000019FL; i++) {
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
        if(!isLitepalinit()) {
            for (long i = 0x00000180L; i <= 0x0000019FL; i++) {
                BreakFMLitepalBean litepalBean = new BreakFMLitepalBean();
                litepalBean.setName("FM" + n);
                litepalBean.setStartDate("0");
                litepalBean.setFreq("0.0");
                litepalBean.setStartTime("00:00");
                litepalBean.setEndTime("00:00");
                litepalBean.setVolume("0");
                litepalBean.setIsSetUp("false");
                litepalBean.setAddress("");
                litepalBean.setData("");
                litepalBean.save();
                n++;
            }
        }
        List<String> isSetUp = BreakFMLitepalUtil.getIsSetUpId();
        if (isSetUp != null){
            for (String a:isSetUp){
                Log.e("fengpj","重新设置已经修改过的行  == " +a);
                int id = Integer.parseInt(a);
                String data = getData(id);
                DateUtil.updataAlarmForBrFM(id,getStartDate(data),getFreq(data),
                        getStartTime(data),getEndTime(data),getVolume(data));
            }
        }

    }


    /**
     * 获取当前修改的行
     * */
    public static int getCorrectLine(long address){
        for (int i=1; i <= 32; i++){
            if (BREAKING_FM_SHOW_LIST.get(i-1) == address){
                return i;
            }
        }
        return 0;
    }

    /**
     * 修改指定行fm信息
     * */
    public static void updateLine(long address,String data){
        BreakFMLitepalBean litepalBean = new BreakFMLitepalBean();
        litepalBean.setFreq(getFreq(data));
        litepalBean.setStartDate(getStartDate(data));
        litepalBean.setStartTime(getStartTime(data));
        litepalBean.setEndTime(getEndTime(data));
        litepalBean.setVolume(getVolume(data));
        litepalBean.setIsSetUp("true");
        litepalBean.setData(data);
        litepalBean.setAddress(""+address);
        litepalBean.updateAll("name = ?","FM" + getCorrectLine(address));
        DateUtil.updataAlarmForBrFM(getCorrectLine(address),getStartDate(data),getFreq(data),
                getStartTime(data),getEndTime(data),getVolume(data));
    }

    /**
     * 判断指定行FM信息是否更新
     * */
    public static boolean isUpdataSuccess(long address,String data){
        int id = getCorrectLine(address);
        String freq = getFreq(data);
        String weeklyRecord = getStartDate(data);
        String startTime = getStartTime(data);
        String endTime = getEndTime(data);
        String volume = getVolume(data);
        if(freq.equals(getFreq(id))
                && weeklyRecord.equals(getStartDate(id))
                && startTime.equals(getStartTime(id))
                && endTime.equals(getEndTime(id))
                && volume.equals(getVolume(id))){
            return true;
        }
        return false;
    }

    /*数据示例 wd,00000181,20170621/105.300/090000/100000/0xxxx*/
    /**
     * 获取插入广播开始的日期
     * */
    public static String getStartDate(String data){
        String startDate = data.substring(0,8);
        return startDate;
    }

    /**
     * 获取插入广播的频率
     * */
    public static String getFreq(String data){
        String freq =data.substring(9,16);
        return freq;
    }

    /**
     * 获取插入广播的开始时间
     * */
    public static String getStartTime(String data){
        String startTime = data.substring(17,23);
        return startTime;
    }

    /**
     *获取插入广播的结束时间
     * */
    public static String getEndTime(String data){
        String endTime = data.substring(24,30);
        return endTime;
    }

    /**
     * 获取插入广播的音量
     * */
    public static String getVolume(String data){
        String volume = data.substring(31);
        return volume;
    }

    /**
     * 获取数据库中的weeklyrecord
     * */
    public static String getStartDate(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","startDate").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean startDate:litepalBean){
                return startDate.getStartDate();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的startTime
     * */
    public static String getStartTime(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","startTime").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean startTime:litepalBean){
                return startTime.getStartTime();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的endTime
     * */
    public static String getEndTime(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","endTime").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean endTime:litepalBean){
                return endTime.getEndTime();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的freq
     * */
    public static String getFreq(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","freq").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean freq:litepalBean){
                return freq.getFreq();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的volume
     * */
    public static String getVolume(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","volume").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean volume:litepalBean){
                return volume.getVolume();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的name id
     * */
    public static String getNameId(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","name").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean name:litepalBean){
                return name.getName().substring(2);
            }
        }
        return null;
    }

    /**
     * 获取数据库中的isSetUp
     * */
    public static String getIsSetUp(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","isSetUp").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean name:litepalBean){
                return name.getIsSetUp();
            }
        }
        return null;
    }

    /**
     * 获取isSrtUp 为true的fmid
     * */
    public static List<String> getIsSetUpId(){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","isSetUp").where("isSetUp = ?","true").find(BreakFMLitepalBean.class);
        List<String> isSetUpList =new ArrayList<String>();
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean name:litepalBean){
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
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","data").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean data:litepalBean){
                return data.getData();
            }
        }
        return null;
    }

    /**
     * 获取数据库中的address
     * */
    public static String getAddress(int fmId){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","address").where("name = ?","FM"+fmId).find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
            for (BreakFMLitepalBean address:litepalBean){
                return address.getAddress();
            }
        }
        return null;
    }

    /**
     * 判断数据库是否已经初始化
     * */
    public static boolean isLitepalinit(){
        List<BreakFMLitepalBean> litepalBean= LitePal.select("name","name").where("name = ?","FM32").find(BreakFMLitepalBean.class);
        if(litepalBean.size() > 0){
                return true;
        }
        return false;
    }
}
