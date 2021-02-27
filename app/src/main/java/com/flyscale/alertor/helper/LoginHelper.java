package com.flyscale.alertor.helper;

import com.flyscale.alertor.data.persist.PersistConfig;

/**
 * 为了方便测试及软件发布，修改地址配置逻辑：
 * 是否使用飞图测试？使用飞图内部地址:判断量测使用客户测试地址
 * 是否使用客户测试地址？使用客户测试地址：使用正式服务器地址
 */
public class LoginHelper {

    public static String getHostname(){
        boolean flyDebug = PersistConfig.findConfig().isFlyDebug();
        boolean customerDebug = PersistConfig.findConfig().isCustomerDebug();
        if (flyDebug){
            return PersistConfig.findConfig().getTcpHostnameFly();
        }else {
            if (customerDebug){
                return PersistConfig.findConfig().getTcpHostNameDebug1();
            }
            return PersistConfig.findConfig().getTcpHostNameRelease1();
        }
    }

    public static String getHttpDownloadUrl(){
        boolean flyDebug = PersistConfig.findConfig().isFlyDebug();
        boolean customerDebug = PersistConfig.findConfig().isCustomerDebug();
        if (flyDebug){
            return PersistConfig.findConfig().getHttpDownloadUrlFly();
        }else {
            if (customerDebug){
                return PersistConfig.findConfig().getHttpDownloadUrlDebug();
            }
            return PersistConfig.findConfig().getHttpDownloadUrl();
        }
    }

    public static int getServerPort(){
        boolean flyDebug = PersistConfig.findConfig().isFlyDebug();
        boolean customerDebug = PersistConfig.findConfig().isCustomerDebug();
        if (flyDebug){
            return PersistConfig.findConfig().getTcpPortFly();
        }else {
            if (customerDebug){
                return PersistConfig.findConfig().getTcpPortDebug();
            }
            return PersistConfig.findConfig().getTcpPortRelease();
        }
    }

}
