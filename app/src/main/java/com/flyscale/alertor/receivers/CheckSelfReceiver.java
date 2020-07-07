package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/7/6 11:49
 * @DESCRIPTION 一键自检功能，所谓自检就是检测联网是否正常、服务器连接是否正常，
 * 如果检测到联网成功就报“网络连接成功”，失败则提示“网络连接失败”，检测到服务器连接成功就提示“连接服务器成功”，失败则提示“连接服务器失败”
 */
public class CheckSelfReceiver extends BroadcastReceiver {
    String TAG = "CheckSelfReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceive: " + intent.getAction());

        if(TextUtils.equals(intent.getAction(),BRConstant.ACTION_CHECK_SELF)){
            if(NetHelper.isNetworkConnected(context)){
                MediaHelper.play(MediaHelper.NET_CONNECT_SUCCESS,true);
            }else {
                MediaHelper.play(MediaHelper.NET_CONNECT_FAIL,true);
            }
            if(NettyHelper.getInstance().isConnect()){
                MediaHelper.play(MediaHelper.SERVER_CONNECT_SUCCESS,true);
            }else {
                MediaHelper.play(MediaHelper.SERVER_CONNECT_FAIL,true);
            }
        }
    }
}
