package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.LoginHelper;

public class EnvSettingsReceiver extends BroadcastReceiver {
    private static final String MODE_PARAM = "mode";
    private static final String FLY_DEBUG = "fly";
    private static final String CUSTOMER_DEBUG = "debug";
    private static final String CUSTOMER_RELEASE = "product";

    private static final String HOSTNAME_PARAM = "hostname";
    private static final String PORT_PARAM = "port";
    private static final String DOWNLOAD_URL_PARAM = "download_url";

    @Override
    public void onReceive(Context context, Intent intent) {
        DDLog.i("action=" + intent.getAction());
        if (TextUtils.equals("com.flyscale.ENV_SETTINGS.MODE", intent.getAction())) {
            //配置模式
            String mode = intent.getStringExtra(MODE_PARAM);
            DDLog.i("mode=" + mode);
            if (TextUtils.equals(mode, FLY_DEBUG)) {
                PersistConfig.saveFlyDebug(true);
                PersistConfig.saveCustomerDebug(false);
            } else if (TextUtils.equals(mode, CUSTOMER_DEBUG)) {
                PersistConfig.saveFlyDebug(false);
                PersistConfig.saveCustomerDebug(true);
            } else if (TextUtils.equals(mode, CUSTOMER_RELEASE)) {
                PersistConfig.saveFlyDebug(false);
                PersistConfig.saveCustomerDebug(false);
            }
        } else if (TextUtils.equals("com.flyscale.ENV_SETTINGS.PARAM", intent.getAction())) {
            String mode = intent.getStringExtra("mode");
            String hostname = intent.getStringExtra(HOSTNAME_PARAM);
            int port = intent.getIntExtra(PORT_PARAM, 0);
            String downloadUrl = intent.getStringExtra(DOWNLOAD_URL_PARAM);
            DDLog.i("mode=" + mode + ",hostname=" + hostname + ",port=" + port + ",downloadUrl=" + downloadUrl);
            if (TextUtils.equals(mode, FLY_DEBUG)) {
                PersistConfig.saveTcpHostnameFly(hostname);
                PersistConfig.saveTcpPortFly(port);
                PersistConfig.saveHttpDownloadUrlFly(downloadUrl);
            } else if (TextUtils.equals(mode, CUSTOMER_DEBUG)) {
                PersistConfig.saveTcpHostNameDebug1(hostname);
                PersistConfig.saveTcpPortDebug(port);
                PersistConfig.saveHttpDownloadUrlFly(downloadUrl);
            } else if (TextUtils.equals(mode, CUSTOMER_RELEASE)) {
                PersistConfig.saveTcpHostNameRelease1(hostname);
                PersistConfig.saveTcpPortRelease(port);
                PersistConfig.saveHttpDownloadUrl(downloadUrl);
            }
        } else if (TextUtils.equals("com.flyscale.ENV_SETTINGS.QUERY", intent.getAction())) {
            boolean flyDebug = PersistConfig.findConfig().isFlyDebug();
            boolean customerDebug = PersistConfig.findConfig().isCustomerDebug();
            String tcpHostnameFly = PersistConfig.findConfig().getTcpHostnameFly();
            int tcpPortFly = PersistConfig.findConfig().getTcpPortFly();
            String httpDownloadUrlFly = PersistConfig.findConfig().getHttpDownloadUrlFly();

            String tcpHostNameDebug1 = PersistConfig.findConfig().getTcpHostNameDebug1();
            int tcpPortDebug = PersistConfig.findConfig().getTcpPortDebug();
            String httpDownloadUrlDebug = PersistConfig.findConfig().getHttpDownloadUrlDebug();

            String tcpHostNameRelease1 = PersistConfig.findConfig().getTcpHostNameRelease1();
            int tcpPortRelease = PersistConfig.findConfig().getTcpPortRelease();
            String httpDownloadUrl = PersistConfig.findConfig().getHttpDownloadUrl();

            StringBuilder sb = new StringBuilder();
            sb.append("flyDebug").append(":").append(flyDebug).append(" \n");
            sb.append("customerDebug").append(":").append(customerDebug).append(" \n");

            sb.append("tcpHostnameFly").append(":").append(tcpHostnameFly).append(" \n");
            sb.append("tcpPortFly").append(":").append(tcpPortFly).append(" \n");
            sb.append("httpDownloadUrlFly").append(":").append(httpDownloadUrlFly).append(" \n");

            sb.append("tcpHostNameDebug1").append(":").append(tcpHostNameDebug1).append(" \n");
            sb.append("tcpPortDebug").append(":").append(tcpPortDebug).append(" \n");
            sb.append("httpDownloadUrlDebug").append(":").append(httpDownloadUrlDebug).append(" \n");

            sb.append("tcpHostNameRelease1").append(":").append(tcpHostNameRelease1).append(" \n");
            sb.append("tcpPortRelease").append(":").append(tcpPortRelease).append(" \n");
            sb.append("httpDownloadUrl").append(":").append(httpDownloadUrl).append(" \n");

            DDLog.i("当前配置信息：" + sb.toString());
            String hostname = LoginHelper.getHostname();
            int serverPort = LoginHelper.getServerPort();
            String httpDownloadUrl1 = LoginHelper.getHttpDownloadUrl();
            DDLog.i("当前有效地址信息：" + hostname + ":" + serverPort + ", " + httpDownloadUrl1);
        }
    }
}
