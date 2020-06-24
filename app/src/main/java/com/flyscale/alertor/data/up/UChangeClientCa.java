package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:54
 * @DESCRIPTION 2.19终端更换CA证书
 * 2.19.1说明
 * 终端更换CA证书，平台向终端发送CA证书文件（十六进制字符串），
 * 终端收到文件后，使用新的CA证书连接到平台新地址，并向新地址回复报文，如果连接新地址失败，重新连接到旧地址平台，并回复报文。
 */
public class UChangeClientCa extends UDefaultChange {

    public UChangeClientCa(String changeResult,String tradeNum) {
        super(changeResult,tradeNum);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_CLIENT_CA_U;
    }
}
