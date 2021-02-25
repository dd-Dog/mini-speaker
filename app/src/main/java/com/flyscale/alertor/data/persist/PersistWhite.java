package com.flyscale.alertor.data.persist;

import android.text.TextUtils;

import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.ListHelper;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
import org.litepal.crud.callback.SaveCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 18:31
 * @DESCRIPTION 暂无
 */
public class PersistWhite extends LitePalSupport {
    String receiveNum = "16886119";
    String index = "0";

    public String getIndex() {
        return index;
    }

    public PersistWhite setIndex(String index) {
        this.index = index;
        return this;
    }

    public static void deleteNumByIndex(String index) {
        LitePal.deleteAll(PersistWhite.class, "index = ?", index);
    }

    public String getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(String receiveNum) {
        this.receiveNum = receiveNum;
    }

    /**
     * 没有数据则初始化白名单
     *
     * @return
     */
    public static List<PersistWhite> findList() {
        List<PersistWhite> list = LitePal.findAll(PersistWhite.class);
        if (ListHelper.isValidCollection(list)) {
            return list;
        } else {
            PersistWhite persistWhite = new PersistWhite();
            persistWhite.save();
            return LitePal.findAll(PersistWhite.class);
        }
    }

    public static void saveNum(String index, String num) {
        PersistWhite persistWhite = new PersistWhite();
        if (TextUtils.equals(index, "0")) {
            DDLog.i("index不能为0，保存失败！");
            return;
        }
        persistWhite.setIndex(index);
        persistWhite.setReceiveNum(num);
        persistWhite.save();
    }

    /**
     * 添加或删除的白名单号码集合
     * @param list
     */
    public static void saveList(List<String> list) {
        try {
            for (int i = 0 ; i < list.size() ; i++) {
                saveNum(i + 1 + "" , list.get(i));
            }
        } catch (Exception e) {

        }
    }

    public static void deleteList(List<String> list) {
        try {
            for(String item : list){
                deleteNum(item);
            }
        } catch (Exception e) {
        }
    }

    public static void deleteNum(String num) {
        LitePal.deleteAll(PersistWhite.class, "receiveNum = ?", num);
    }

    public static void deleteAllNum() {
        LitePal.deleteAll(PersistWhite.class, "receiveNum != ?", PersistConfig.findConfig().getAlarmNum());
    }

    public static boolean isContains(String num) {
        if (TextUtils.isEmpty(num)) {
            return false;
        }
        List list = LitePal.where("receiveNum = ?", num).find(PersistWhite.class);
        return ListHelper.isValidCollection(list);
    }
}
