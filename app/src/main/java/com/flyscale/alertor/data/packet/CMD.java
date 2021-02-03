package com.flyscale.alertor.data.packet;

public enum CMD {
    //rd:读数据;wd:写数据;ra:读响应，wa写响应
    READ("rd"), WRITE("wd"), READ_ANSWER("ra"), WRITE_ANSWER("wa"), UNKOWN("unkown");

    private String value;
    CMD(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CMD getCMD(String value){
        switch (value){
            case "rd":
                return READ;
            case "wd":
                return WRITE;
            case "ra":
                return READ_ANSWER;
            case "wa":
                return WRITE_ANSWER;
        }
        return UNKOWN;
    }
}
