package com.flyscale.alertor.data.packet;

import com.flyscale.alertor.helper.CRC16Helper;
import com.flyscale.alertor.helper.DESUtil;

import java.nio.ByteBuffer;

public class TcpPacket {
    /**
     * 报文第一部分	2字节长度命令码(rd:读数据;wd:写数据;ra:读响应，wa写响应，英文逗号结束
     * 报文第二部分	8字节长度，数据地址(00000000-ffffffff);英文逗号结束
     * 报文第三部分	32字节长度，报文数据(asc编码); 没有结束的英文逗号
     * 报文第四部分	4字节CRC16校验码;没有结束的英文分号
     * 报文第五部分	结尾标志：0x0d 0x0a，2字节结尾标志，报文其余地方不允许出现这两个字节
     * 每个报文长度固定为48+2=50字节，报文在传输时前48字节需做DES加密处理。
     */
    public static final int PACKET_LENGTH = 50;

    private byte[] bytes = new byte[PACKET_LENGTH];//TCP报文数据
    private byte[] decodedBytes = new byte[48];//解密后的数据，去掉结尾符

    private byte[] cmdBytes = new byte[2];//命令字节
    private byte[] addressBytes = new byte[8];//地址字节
    private byte[] dataBytes = new byte[32];//有效数据字节
    private byte[] crc = new byte[4];   //CRC16校验码字节
    private byte[] endFlag = {0x0d, 0x0a};

    /*解析后的有效数据*/
    private CMD cmd;    //命令
    private long address;//数据地址
    private String data;    //有效数据


    private TcpPacket(byte[] data) {
        setBytes(data);
    }

    private void parse() {
        if (this.dataBytes[48] != endFlag[0] || this.dataBytes[49] != endFlag[1]){
            System.out.println("未找到结束标识符！");
            return;
        }
        //DES解密
        byte[] encodedBytes = new byte[48];
        System.arraycopy(bytes, 0, encodedBytes, 0, 48);
        decodedBytes = DESUtil.decode("1234", encodedBytes);
        assert decodedBytes != null;
        if (decodedBytes.length != 48) {
            //解密失败
            System.out.println("解密失败！");
            return;
        }
        //CRC校验
        int crc16 = CRC16Helper.calcCrc16(decodedBytes);
        if (crc16 != 0) {
            System.out.println("校验失败！");
            return;
        }

        //解析字段
        int index = 0;
        System.arraycopy(decodedBytes, index, cmdBytes, 0, 2);
        index += 2;
        System.arraycopy(decodedBytes, index, addressBytes, 0, 8);
        index += 8;
        System.arraycopy(decodedBytes, index, dataBytes, 0, 32);
        index += 32;
        System.arraycopy(decodedBytes, index, crc, 0, 4);

        //解析命令
        String cmdStr = "";
        cmdStr += (char)cmdBytes[0];
        cmdStr += (char)cmdBytes[1];
        cmd = CMD.getCMD(cmdStr);
        //解析地址
        address = byteArrayToLong(addressBytes);
        //解析有效数据
        data = new String(dataBytes);

    }

    private static long byteArrayToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }

    /**
     * 构造一个TcpPacket
     *
     * @param data
     * @return
     */
    public static TcpPacket getInstance(byte[] data) {
        return new TcpPacket(data);
    }

    public byte[] getBytes() {
        return bytes;
    }

    private void setBytes(byte[] bytes) {
        if (bytes == null) {
            return;
        }
        //拷贝长度不超过50个字节
        int copyLen = Math.min(bytes.length, 50);
        System.arraycopy(bytes, 0, this.bytes, 0, copyLen);
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
}
