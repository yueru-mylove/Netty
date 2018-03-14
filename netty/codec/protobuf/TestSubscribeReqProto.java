package com.phei.netty.codec.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Assert;
import org.springframework.http.StreamingHttpOutputMessage;

import java.util.ArrayList;
import java.util.List;

public class TestSubscribeReqProto {

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1);
        builder.setUserName("burning");
        builder.setProductName("Netty权威指南");
//        List<String> address = new ArrayList<String>();
//        address.add("nanjing yuhuatai");
//        address.add("beijing tiantan");
//        address.add("shenzhen hongshulin");
        builder.setAddress("NANJING YUHUATAI");
        return builder.build();
    }


    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq subscribeReq = createSubscribeReq();
        System.out.println("before encode..." + subscribeReq.toString());
        SubscribeReqProto.SubscribeReq decode = decode(encode(subscribeReq));
        System.out.println("after encode..." + decode.toString());
        Assert.assertEquals(subscribeReq, decode);
    }
}

