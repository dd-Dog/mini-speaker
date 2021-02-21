package com.flyscale.alertor.data.packet;

import android.text.TextUtils;

import com.flyscale.alertor.helper.CRC16Helper;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.jni.NativeHelper;

public class TcpPacket {
    /**
     * 报文第一部分	2字节长度命令码(rd:读数据;wd:写数据;ra:读响应，wa写响应，英文逗号结束
     * 报文第二部分	8字节长度，数据地址(00000000-ffffffff);英文逗号结束
     * 报文第三部分	32字节长度，报文数据(asc编码); 没有结束的英文逗号
     * 报文第四部分	4字节CRC16校验码;没有结束的英文分号
     * 报文第五部分	结尾标志：0x0d 0x0a，2字节结尾标志，报文其余地方不允许出现这两个字节
     * 每个报文长度固定为48+2=50字节，报文在传输时前48字节需做DES加密处理。
     */
    public static final int PACKET_LENGTH = 50;//数据包总长度
    public static final int CMD_LENGTH = 2;//命令长度
    public static final int ADDRESS_LENGTH = 8;//地址长度
    public static final int DATA_LENGTH = 32;//有效数据长度
    public static final int CRC_LENGTH = 4;//crc校验码长度
    public static final int END_FLAG_LENGTH = 2;//结束符长度

    public static final int CRC_TEXT_LENGTH = PACKET_LENGTH - END_FLAG_LENGTH - CRC_LENGTH;//CRC校验的数据长度
    public static final int ENCODE_LENGTH = PACKET_LENGTH - END_FLAG_LENGTH;//DES加密数据长度


    public static final byte[] SEPARATOR = {','};


    private byte[] tcpBytes;//TCP报文数据
    private byte[] encodedBytes;//加密数据
    private byte[] decodedBytes = new byte[ENCODE_LENGTH];//解密后的数据

    private byte[] cmdBytes = new byte[CMD_LENGTH];//命令字节
    private byte[] addressBytes = new byte[ADDRESS_LENGTH];//地址字节
    private byte[] dataBytes = new byte[DATA_LENGTH];//有效数据字节
    private byte[] crcBytes = new byte[CRC_LENGTH];   //CRC16校验码字节
    private final byte[] endFlagBytes = {0x0d, 0x0a};

    /*解析后的有效数据*/
    private CMD cmd;    //命令
    private long address;//数据地址
    private String data;    //有效数据


    private TcpPacket(byte[] data) {
        setTcpBytes(data);
    }

    private TcpPacket() {
    }

    private void parse() {
        if (tcpBytes == null || tcpBytes.length < 2) {
            System.out.println("数据解析失败，无效的数据！");
            return;
        }
        if (this.tcpBytes[tcpBytes.length - 2] != endFlagBytes[0] || this.tcpBytes[tcpBytes.length - 1] != endFlagBytes[1]) {
            System.out.println("未找到结束标识符！");
            return;
        }

        //DES解密
        encodedBytes = new byte[tcpBytes.length - END_FLAG_LENGTH];
        System.arraycopy(tcpBytes, 0, encodedBytes, 0, encodedBytes.length);
        System.out.println("待解密数据：");
        System.out.println(DDLog.printArrayHex(encodedBytes));

        decodedBytes = NativeHelper.decrypt(encodedBytes);
        System.out.println("解密明文数据：");
        System.out.println(DDLog.printArrayHex(decodedBytes));

        assert decodedBytes != null;
        if (decodedBytes.length != ENCODE_LENGTH) {
            //解密失败
            System.out.println("解密失败！");
            return;
        }
        //CRC校验
        //计算出校验码long类型
        try {
            byte[] crcReadyBytes = new byte[PACKET_LENGTH - 4];
            char c1 = (char) decodedBytes[44];
            char c2 = (char) decodedBytes[45];
            char c3 = (char) decodedBytes[46];
            char c4 = (char) decodedBytes[47];
            int high = Integer.parseInt("" + c1 + c2, 16);
            int low = Integer.parseInt("" + c3 + c4, 16);
            DDLog.i("high=" + high + ",low=" + low);
            crcReadyBytes[44] = (byte) low;
            crcReadyBytes[45] = (byte) high;
            System.arraycopy(decodedBytes, 0, crcReadyBytes, 0, 44);
            int crc16 = CRC16Helper.calcCrc16IBM(crcReadyBytes);
            if (crc16 != 0) {
                System.out.println("校验失败！");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            DDLog.e("CRC校验失败");
            return;
        }
        //解析字段
        int index = 0;
        System.arraycopy(decodedBytes, index, cmdBytes, 0, cmdBytes.length);
        index += cmdBytes.length;
        index += SEPARATOR.length;//跳过分隔符
        System.arraycopy(decodedBytes, index, addressBytes, 0, addressBytes.length);
        index += addressBytes.length;
        index += SEPARATOR.length;//跳过分隔符
        DDLog.i(DDLog.printArrayHex(addressBytes));
        System.arraycopy(decodedBytes, index, dataBytes, 0, dataBytes.length);
        index += dataBytes.length;
        System.arraycopy(decodedBytes, index, crcBytes, 0, cmdBytes.length);

        //解析命令
        String cmdStr = new String(cmdBytes);
        cmd = CMD.getCMD(cmdStr);
        //解析地址
        address = Long.parseLong(new String(addressBytes), 16);
        //解析有效数据
        data = new String(dataBytes);

    }

    /**
     * 加密数据，并生成TcpPacket对象，用于向服务器发送
     *
     * @param cmd     命令
     * @param address 地址
     * @param data    有效数据
     * @return
     */
    public TcpPacket encode(CMD cmd, long address, String data) {
        DDLog.i("encode: cmd=" + cmd + ",address=" + address + ",data=" + data);
        //数据长度判断
        if (TextUtils.isEmpty(data) || data.length() > 32) {
            DDLog.i("数据错误！");
            return null;
        }
        this.cmd = cmd;
        this.address = address;
        this.data = data;

        //拼接明文字节数据
        cmdBytes = cmd.getValue().getBytes();
        //生成地址，十六进制用ASCII表示，长度为8字节
        String addressStr = Long.toHexString(address);
        if (addressStr.length() < 8) {
            for (int i = addressStr.length(); i < 8; i++) {
                addressStr = "0" + addressStr;
            }
        }
        addressBytes = addressStr.getBytes();
        DDLog.i("address=" + addressStr + ",bytes=" + DDLog.printArrayHex(addressBytes));

        dataBytes = data.getBytes();
        System.out.println(DDLog.printArrayHex(cmdBytes));
        System.out.println(DDLog.printArrayHex(addressBytes));
        System.out.println(DDLog.printArrayHex(dataBytes));

        int index = 0;
        //拼接命令
        System.arraycopy(cmdBytes, 0, decodedBytes, index, cmdBytes.length);
        index += cmdBytes.length;
        //拼接分隔符
        System.arraycopy(SEPARATOR, 0, decodedBytes, index, SEPARATOR.length);
        index += SEPARATOR.length;
        //拼接地址
        System.arraycopy(addressBytes, 0, decodedBytes, index, addressBytes.length);
        index += addressBytes.length;
        //拼接分隔符
        System.arraycopy(SEPARATOR, 0, decodedBytes, index, SEPARATOR.length);
        index += SEPARATOR.length;
        //拼接有效数据
        System.arraycopy(dataBytes, 0, decodedBytes, index, dataBytes.length);
        index += dataBytes.length;

        DDLog.i("拼接字数据完成：");
        DDLog.i(DDLog.printArrayHex(decodedBytes));

        //CRC校验，准备待校验数据
        byte[] readyBytes = new byte[CRC_TEXT_LENGTH];
        System.arraycopy(decodedBytes, 0, readyBytes, 0, readyBytes.length);
        DDLog.i("待校验数据：");
        DDLog.i(DDLog.printArrayHex(readyBytes));
        //生成校验数据
        int crcCode = CRC16Helper.calcCrc16IBM(readyBytes);
        String crcHexStr = Integer.toHexString(crcCode);
        if (crcHexStr.length() < 4) {
            for (int i = crcHexStr.length(); i < 4; i++) {
                crcHexStr = "0" + crcHexStr;
            }
        }
        crcBytes = crcHexStr.getBytes();
        DDLog.i("crcHexStr=" + crcHexStr + ",bytes=" + DDLog.printArrayHex(crcBytes));
        //拼接校验数据
        System.arraycopy(crcBytes, 0, decodedBytes, index, crcBytes.length);
        index += crcBytes.length;

        System.out.println("待加密数据：");
        System.out.println(DDLog.printArrayHex(decodedBytes));
        //对明文DES加密
        encodedBytes = NativeHelper.encrypt(decodedBytes);
        System.out.println("加密数据：");
        System.out.println(DDLog.printArrayHex(encodedBytes));
        //将密文数据放入等发送的数据包
        if (encodedBytes != null) {
            tcpBytes = new byte[encodedBytes.length + END_FLAG_LENGTH];
            System.arraycopy(encodedBytes, 0, tcpBytes, 0, encodedBytes.length);
        } else {
            System.out.println("加密失败！");
            return null;
        }
        //最后拼接结束符
        System.arraycopy(endFlagBytes, 0, tcpBytes, encodedBytes.length, endFlagBytes.length);
        System.out.println("TCP数据：");
        System.out.println(DDLog.printArrayHex(tcpBytes));
        return this;
    }

    /**
     * 从密文数据中解码出一个TcpPacket对象，用于接收服务器消息
     *
     * @param data
     * @return
     */
    public static TcpPacket decode(byte[] data) {
        return new TcpPacket(data);
    }

    public static TcpPacket getInstance() {
        return new TcpPacket();
    }

    public byte[] getTcpBytes() {
        return tcpBytes;
    }

    /**
     * 接收数据
     *
     * @param tcpBytes
     */
    private void setTcpBytes(byte[] tcpBytes) {
        if (tcpBytes == null) {
            return;
        }
        //拷贝长度不超过50个字节
        this.tcpBytes = tcpBytes;
        parse();//解析数据
    }

    public CMD getCmd() {
        return cmd;
    }

    public long getAddress() {
        return address;
    }

    public String getData() {
        return data;
    }


    @Override
    public String toString() {
        return "TcpPacket{" +
                "cmd=" + cmd +
                ", address=" + address +
                ", data='" + data + '\'' +
                '}';
    }

    /**
     * 加密数据，并生成TcpPacket对象，用于向服务器发送
     *
     * @param blank 空白命令行
     * @return
     */
    public TcpPacket encode(String blank) {
        this.data = blank;
        decodedBytes = data.getBytes();
        //对明文DES加密
        encodedBytes = NativeHelper.encrypt(decodedBytes);
        //将密文数据放入等发送的数据包
        if (encodedBytes != null) {
            tcpBytes = new byte[encodedBytes.length + END_FLAG_LENGTH];
            System.arraycopy(encodedBytes, 0, tcpBytes, 0, 48);
        } else {
            System.out.println("加密失败！");
            return null;
        }
        //最后拼接结束符
        System.arraycopy(endFlagBytes, 0, tcpBytes, encodedBytes.length, endFlagBytes.length);
        System.out.println("TCP数据：");
        System.out.println(DDLog.printArrayHex(tcpBytes));
        return this;
    }
}
