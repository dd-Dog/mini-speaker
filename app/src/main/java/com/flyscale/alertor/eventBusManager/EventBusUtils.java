package com.flyscale.alertor.eventBusManager;

import org.greenrobot.eventbus.EventBus;

/**
 * 作者 ： 高鹤泉
 * 时间 ： 2019/7/5 下午6:03
 */
public class EventBusUtils {

    public static void postMessage(EventMessage message){
        EventBus.getDefault().post(message);
    }

    public static void postMessage(int type){
        EventMessage message = new EventMessage(type,null);
        postMessage(message);
    }

    public static void postMessage(int type,Object object){
        EventMessage message = new EventMessage(type,object);
        postMessage(message);
    }
}
