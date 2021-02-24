package com.flyscale.alertor.data.persist;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by liChang on 2021/2/24
 */
public class PersistPacket extends LitePalSupport {

    String cmd;
    long address;
    String data;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static PersistPacket savePacket(String cmd, long address, String data) {
        PersistPacket persistPacket = new PersistPacket();
        //避免插入重复数据
        List<PersistPacket> songs = LitePal.where("cmd = ? and address = ? and data = ? ",
                cmd, String.valueOf(address), data).find(PersistPacket.class);
        if (songs == null || songs.size() == 0) {
        } else {
            for (int i = 0; i < songs.size(); i++) {
                songs.get(i).delete();
            }
        }
        persistPacket.setCmd(cmd);
        persistPacket.setAddress(address);
        persistPacket.setData(data);
        persistPacket.save();
        return persistPacket;
    }

}
