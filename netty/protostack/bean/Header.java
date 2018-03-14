package com.phei.netty.protostack.bean;

import java.util.HashMap;
import java.util.Map;

public final class Header {

    // netty消息校验码， 由三部分组成，
    // 0xabef固定值，表明是netty消息
    // 主版本号 １个字节　次版本号　１个字节　０～２５５
    private int crcCode = 0xabef0101;
    // 消息长度
    private int length;

    // 集群节点全局唯一， 由绘画id生成器生成
    private long sessionID;
    // 消息类型
    private byte type;

    // 消息优先级
    private byte priority;
    // 附件
    private Map<String, Object> attachment = new HashMap<String, Object>();

    public Header() {
    }

    public int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public long getSessionID() {
        return sessionID;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }

    @Override
    public String toString() {
        return "Header{" +
                "crcCode=" + crcCode +
                ", length=" + length +
                ", sessionID=" + sessionID +
                ", type=" + type +
                ", priority=" + priority +
                ", attachment=" + attachment +
                '}';
    }
}
