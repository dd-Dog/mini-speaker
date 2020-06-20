package com.flyscale.alertor.data.persist;

import android.text.TextUtils;

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
    String receiveNum = "1183111";

    public String getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(String receiveNum) {
        this.receiveNum = receiveNum;
    }

    public static List<PersistWhite> findList(){
        List<PersistWhite> list = LitePal.findAll(PersistWhite.class);
        if(ListHelper.isValidCollection(list)){
            return list;
        }else {
            PersistWhite persistWhite = new PersistWhite();
            persistWhite.save();
            return LitePal.findAll(PersistWhite.class);
        }
    }

    public static void saveNum(String num){
        PersistWhite persistWhite = new PersistWhite();
        persistWhite.setReceiveNum(num);
        persistWhite.save();
    }

    /**
     * 添加或删除的白名单号码，最少1个，最多无限制，电话号码之间以封号”;”分割
     * @param results
     */
    public static void saveList(String results){
        String[] array = TextUtils.split(results,";");
        for(String item : array){
            saveNum(item);
        }
    }

    public static void deleteList(String results){
        String[] array = TextUtils.split(results,";");
        for(String item : array){
            deleteNum(item);
        }
    }

    public static void deleteNum(String num){
        LitePal.deleteAll(PersistWhite.class,"receiveNum = ?",num);
    }

    public static boolean isContains(String num){
        if(TextUtils.isEmpty(num)){
            return false;
        }
        List list = LitePal.where("receiveNum = ?",num).find(PersistWhite.class);
        return ListHelper.isValidCollection(list);
    }
}
