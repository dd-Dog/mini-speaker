package com.flyscale.alertor.helper;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 13:45
 * @DESCRIPTION 4位循环数：范围0-9999，从0开始，递增赋值，步长为1，增加到9999后，再从0开始
 */
public class Num9999Helper {
    static volatile AtomicInteger mAtomicInteger = new AtomicInteger(-1);

    /**
     * 增加操作后 atomicInteger都变化 所以都已经+1了 .这里初始值从-1开始  就可以是结果从0开始
     * @return
     */
    public static AtomicInteger getAtomicInteger(){
        if(mAtomicInteger.get() == 9999){
            mAtomicInteger = new AtomicInteger(-1);
        }
        mAtomicInteger.incrementAndGet();
        return mAtomicInteger;
    }

    /**
     * 格式化4位循环数
     * @return
     */
    public static String formatNum(){
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        return decimalFormat.format(getAtomicInteger());
    }

}
