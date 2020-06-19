package com.flyscale.alertor.data.persist;

import android.text.TextUtils;

import com.flyscale.alertor.helper.ListHelper;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

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
