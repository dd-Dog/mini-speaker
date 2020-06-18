package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:07
 * @DESCRIPTION 2.15移动终端录音上传
 * 当音频文件上传完成时，终端向平台发送文件上传完成指令，平台端确认在FTP目录找到文件后，回复终端收到音频文件指令。（文件命名规则：ICCID_报警流水号_文件序号.amr）
 */
public class UUpdateRecord extends BaseUpData {

    public UUpdateRecord(String fileNum,String alarmSerNum) {
        this.fileNum = fileNum;
        this.alarmSerNum = alarmSerNum;
    }

    @Override
    public int getType() {
        return TYPE_CLIENT_UPDATE_RECORD_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid(),fileNum,alarmSerNum);
    }
}
