package net.pocrd.define;

import java.io.OutputStream;

import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;

import com.google.protobuf.GeneratedMessage;

public interface Serializer<T> {
    public static final Serializer<String> stringSerializer = new Serializer<String>() {

                                                                @Override
                                                                public void toXml(String instance, OutputStream out, boolean isRoot) {
                                                                    try {
                                                                        out.write(instance.getBytes(ConstField.UTF8));
                                                                    } catch (Exception e) {
                                                                        throw new ReturnCodeException(ReturnCode.UNKNOWN_ERROR, e);
                                                                    }
                                                                }

                                                                @Override
                                                                public void toJson(String instance, OutputStream out, boolean isRoot) {
                                                                    try {
                                                                        out.write(instance.getBytes(ConstField.UTF8));
                                                                    } catch (Exception e) {
                                                                        throw new ReturnCodeException(ReturnCode.UNKNOWN_ERROR, e);
                                                                    }
                                                                }

                                                                @Override
                                                                public void toProtobuf(GeneratedMessage instance, OutputStream out) {
                                                                    throw new UnsupportedOperationException("该类型不支持protobuf方式的序列化.");
                                                                }
                                                            };

    void toXml(T instance, OutputStream out, boolean isRoot);

    void toJson(T instance, OutputStream out, boolean isRoot);

    void toProtobuf(GeneratedMessage instance, OutputStream out);
}
