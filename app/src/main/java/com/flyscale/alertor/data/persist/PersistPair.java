package com.flyscale.alertor.data.persist;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * @author 高鹤泉
 * @TIME 2020/7/16 11:41
 * @DESCRIPTION 暂无
 */
public class PersistPair extends LitePalSupport {


    boolean remoteControl = false;
    boolean door;
    boolean infrared;
    boolean gas;
    boolean smoke;



    public boolean isRemoteControl() {
        return remoteControl;
    }

    public void setRemoteControl(boolean remoteControl) {
        this.remoteControl = remoteControl;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public boolean isInfrared() {
        return infrared;
    }

    public void setInfrared(boolean infrared) {
        this.infrared = infrared;
    }

    public boolean isGas() {
        return gas;
    }

    public void setGas(boolean gas) {
        this.gas = gas;
    }

    public boolean isSmoke() {
        return smoke;
    }

    public void setSmoke(boolean smoke) {
        this.smoke = smoke;
    }


    public static PersistPair findPair(){
        PersistPair persistPair = LitePal.findFirst(PersistPair.class);
        if(persistPair == null){
            persistPair = new PersistPair();
            persistPair.save();
        }
        return persistPair;
    }

    public static void saveControl(boolean hasPair){
        PersistPair persistPair = findPair();
        persistPair.setRemoteControl(hasPair);
        persistPair.save();
    }
    public static void saveDoor(boolean hasPair){
        PersistPair persistPair = findPair();
        persistPair.setDoor(hasPair);
        persistPair.save();
    }
    public static void saveInfrared(boolean hasPair){
        PersistPair persistPair = findPair();
        persistPair.setInfrared(hasPair);
        persistPair.save();
    }
    public static void saveGas(boolean hasPair){
        PersistPair persistPair = findPair();
        persistPair.setGas(hasPair);
        persistPair.save();
    }
    public static void saveSmoke(boolean hasPair){
        PersistPair persistPair = findPair();
        persistPair.setSmoke(hasPair);
        persistPair.save();
    }

    public static void clearAll(boolean clearAll){
        if(clearAll){
            saveControl(false);
            saveDoor(false);
            saveInfrared(false);
            saveGas(false);
            saveSmoke(false);
        }
    }
}
