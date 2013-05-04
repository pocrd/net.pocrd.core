package net.pocrd.define;

import java.io.OutputStream;

import com.google.protobuf.GeneratedMessage;

public interface Serializer<T> {
    void toXml(T instance, OutputStream out, boolean isRoot);
    void toJson(T instance, OutputStream out);
    void toProtobuf(GeneratedMessage instance, OutputStream out);
}
