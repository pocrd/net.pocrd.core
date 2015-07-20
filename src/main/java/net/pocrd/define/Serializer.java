package net.pocrd.define;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ReturnCodeException;
import net.pocrd.responseEntity.JSONString;
import net.pocrd.responseEntity.ObjectArrayResp;
import net.pocrd.responseEntity.RawString;
import net.pocrd.util.POJOSerializerProvider;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer<T> {
    /**
     * ApiSerializerFeatures
     */
    public static class ApiSerializerFeature {
        /**
         * 使用fastjson作为json序列化实现，序列化的特性定义
         */
        public static final SerializerFeature[] SERIALIZER_FEATURES = new SerializerFeature[]{
                //            SerializerFeature.DisableCircularReferenceDetect,//disable循环引用
                //            SerializerFeature.WriteMapNullValue,//null属性，序列化为null,do by guankaiqiang,android sdk中 JSON.optString()将null convert成了"null",故关闭该特性
                SerializerFeature.NotWriteRootClassName, //与pocrd保持一致
                //            SerializerFeature.WriteEnumUsingToString, //与pocrd保持一致
                SerializerFeature.WriteNullNumberAsZero,//与pocrd保持一致
                SerializerFeature.WriteNullBooleanAsFalse,//与pocrd保持一致
        };
    }

    /**
     * jsonString的序列化
     */
    public static final Serializer<JSONString> jsonStringSerializer = new Serializer<JSONString>() {

        @Override
        public void toXml(JSONString instance, OutputStream out, boolean isRoot) {
            try {
                if (instance.value != null) {
                    out.write(instance.value.getBytes(ConstField.UTF8));
                }
            } catch (Exception e) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, e);
            }
        }

        @Override
        public void toJson(JSONString instance, OutputStream out, boolean isRoot) {
            try {
                if (instance.value != null) {
                    out.write(instance.value.getBytes(ConstField.UTF8));
                }
            } catch (Exception e) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, e);
            }
        }
    };

    public static final Serializer<RawString> rawStringSerializer = new Serializer<RawString>() {

        @Override
        public void toXml(RawString instance, OutputStream out, boolean isRoot) {
            try {
                if (instance.value != null) {
                    out.write(instance.value.getBytes(ConstField.UTF8));
                }
            } catch (Exception e) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, e);
            }
        }

        @Override
        public void toJson(RawString instance, OutputStream out, boolean isRoot) {
            try {
                if (instance.value != null) {
                    out.write(instance.value.getBytes(ConstField.UTF8));
                }
            } catch (Exception e) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, e);
            }
        }
    };

    /**
     * note:PojoSerializer不支持动态类型，要让SerializerProvider支持要写很多恶心的代码，还是直接写java代码了
     */
    public static final Serializer<ObjectArrayResp> objectArrayRespSerializer = new Serializer<ObjectArrayResp>() {
        byte[][] bs = new byte[8][];
        {
            bs[0] = "<ObjectArrayResp>".getBytes(ConstField.UTF8);
            bs[1] = "<value>".getBytes(ConstField.UTF8);
            bs[2] = "<item>".getBytes(ConstField.UTF8);
            bs[3] = "</item>".getBytes(ConstField.UTF8);
            bs[4] = "</value>".getBytes(ConstField.UTF8);
            bs[5] = "</ObjectArrayResp>".getBytes(ConstField.UTF8);
            bs[6] = "<![CDATA[".getBytes(ConstField.UTF8);
            bs[7] = "]]>".getBytes(ConstField.UTF8);
        }
        @Override
        public void toXml(ObjectArrayResp instance, OutputStream out, boolean isRoot) {
            if (instance == null) {
                return;
            }
            try {
                if (isRoot) {
                    out.write(bs[0]);
                }
                if (instance.value != null) {
                    out.write(bs[1]);
                    for (Object obj : instance.value) {
                        out.write(bs[2]);
                        if (obj != null) {
                            if (obj.getClass() == String.class) {
                                out.write(bs[6]);
                                out.write(obj.toString().getBytes(ConstField.UTF8));
                                out.write(bs[7]);
                            } else if (obj.getClass().isEnum()) {
                                out.write(bs[6]);
                                out.write(((Enum)obj).name().getBytes(ConstField.UTF8));
                                out.write(bs[7]);
                            } else {
                                Serializer localSerializer = POJOSerializerProvider.getSerializer(obj.getClass());
                                localSerializer.toXml(obj, out, false);
                            }
                        }
                        out.write(bs[3]);
                    }
                    out.write(bs[4]);
                }
                if (isRoot) {
                    out.write(bs[5]);
                }
            } catch (IOException localIOException) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, localIOException);
            }
        }
        @Override
        public void toJson(ObjectArrayResp instance, OutputStream out, boolean isRoot) {
            try {
                out.write(JSON.toJSONBytes(instance, ApiSerializerFeature.SERIALIZER_FEATURES));
            } catch (IOException localIOException) {
                throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, localIOException);
            }
        }
    };

    void toXml(T instance, OutputStream out, boolean isRoot);

    void toJson(T instance, OutputStream out, boolean isRoot);
    //    void toProtobuf(GeneratedMessage instance, OutputStream out);

}
