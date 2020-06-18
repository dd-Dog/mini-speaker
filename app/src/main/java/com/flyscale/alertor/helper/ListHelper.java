package com.flyscale.alertor.helper;

import android.text.TextUtils;

import java.util.Collection;
import java.util.List;

public class ListHelper {
    /**
     * 是否是有效的集合
     *
     * @param list
     * @return
     */
    public static boolean isValidList(List list) {
        if (list != null && list.size() > 0)
            return true;
        else
            return false;
    }

    public static boolean isValidCollection(Collection collection){
        if(collection != null && collection.size() > 0){
            return true;
        }
        return false;
    }

    /**
     * 集合转
     * @param delimiter
     * @param list
     * @return
     */
    public static String listConvertString(String delimiter, List<String> list){
        if(!ListHelper.isValidList(list)){
            return null;
        }
        String[] result = list.toArray(new String[list.size()]);
        return TextUtils.join(delimiter,result);
    }

    /**
     * 返回截取的List长度
     * @param end
     * @param list
     * @return
     */
    public static List<?> getSubList(int end, List list){
        if(ListHelper.isValidList(list)){
            if(list.size() > end){
                return list.subList(0,end);
            }else {
                return list;
            }
        }
        return null;
    }

}
