package com.flyscale.alertor.eventBusManager;

/**
 * 高师傅好棒
 *
 * @创建时间 2019-10-08 15:55
 */
public interface EventType {

    int BLT_START_DISCOVER = 1;//蓝牙开始扫描
    int BLT_FINISH_DISCOVER = 2;//蓝牙扫描结束
    int BLT_FIND_DEVICE = 3;//蓝牙发现设备
    int BLT_DEVICE_CHANGE_STATE = 4;//配对设备状态改变
    int BLT_DEVICE_CONNECT_SUCCESS = 5;//蓝牙设备连接成功
    int BLT_CONNECT_FAIL = 6;//蓝牙客户端连接蓝牙服务器失败
    int BLT_RECEIVE_MSG = 7;//蓝牙接到消息
    int BLT_RECEIVE_FAIL = 8;//蓝牙接收消息读失败
    int BLT_SEND_MSG = 9;//蓝牙发消息
}
