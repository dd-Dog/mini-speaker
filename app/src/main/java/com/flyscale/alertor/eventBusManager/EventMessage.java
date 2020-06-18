package com.flyscale.alertor.eventBusManager;

/**
 * 作者 ： 高鹤泉
 * 时间 ： 2019/7/5 下午6:00
 */
public class EventMessage {
    int type;
    Object data;

    public EventMessage(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
