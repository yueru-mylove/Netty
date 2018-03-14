package com.phei.netty.protostack.encoder;

import org.jboss.marshalling.*;

import java.io.IOException;

public class MarshallingCodeFactory {

    /**
     * 创建Marshaller
     *
     * @return
     * @throws IOException
     */
    public static Marshaller buildMarshaller() {
        try {
            final MarshallerFactory factory = Marshalling.getProvidedMarshallerFactory("serial");
            final MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
            marshallingConfiguration.setVersion(5);
            return factory.createMarshaller(marshallingConfiguration);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Unmarshaller buildUnmarshaller() {
        try {
            final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
            final MarshallingConfiguration configuration = new MarshallingConfiguration();
            configuration.setVersion(5);
            Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
            return unmarshaller;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
