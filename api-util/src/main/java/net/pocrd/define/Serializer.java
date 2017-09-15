package net.pocrd.define;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ReturnCodeException;
import net.pocrd.responseEntity.DynamicEntity;
import net.pocrd.responseEntity.JSONString;
import net.pocrd.responseEntity.ObjectArrayResp;
import net.pocrd.util.POJOSerializerProvider;
import net.pocrd.util.RawString;

import java.io.IOException;
import java.io.OutputStream;

public interface Serializer<T> {
    public static final SerializerFeature[] EMPTY_FEATURES = new SerializerFeature[] {};

    /**
     * jsonString的序列化
     */
    static Serializer<JSONString> getJsonStringSerializer() {
        return new Serializer<JSONString>() {

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
    }

    static Serializer<RawString> getRawStringSerializer() {
        return new Serializer<RawString>() {

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
    }

    static Serializer<DynamicEntity> getDynamicEntitySerializer() {
        return new Serializer<DynamicEntity>() {
            byte[][] bs = new byte[8][];

            {
                bs[0] = "<DynamicEntity>".getBytes(ConstField.UTF8);
                bs[1] = "<typeName>".getBytes(ConstField.UTF8);
                bs[2] = "</typeName>".getBytes(ConstField.UTF8);
                bs[3] = "<entity>".getBytes(ConstField.UTF8);
                bs[4] = "</entity>".getBytes(ConstField.UTF8);
                bs[5] = "</DynamicEntity>".getBytes(ConstField.UTF8);
                bs[6] = "<![CDATA[".getBytes(ConstField.UTF8);
                bs[7] = "]]>".getBytes(ConstField.UTF8);

                // 目前版本的fastjson有bug导致该value filter和fastjson的DisableCircularReferenceDetect配置同时开启时,该value filter会被忽略
                // 具体原因是当A类包含一个B类成员变量时,即使B类已注册value filter但是当DisableCircularReferenceDetect打开时,A类序列化时会忽略所有
                // 成员的value filter. 暂时通过设置DynamicEntity的成员为final来规避typeName和entity类型不匹配的问题。
                //                SerializeConfig.getGlobalInstance().addFilter(DynamicEntity.class, new ValueFilter() {
                //
                //                    @Override
                //                    public Object process(Object object, String name, Object value) {
                //                        if ("typeName".equals(name)) {
                //                            Object entity = ((DynamicEntity)object).entity;
                //                            if (entity != null) {
                //                                return entity.getClass().getSimpleName();
                //                            }
                //                        }
                //                        return value;
                //                    }
                //                });
            }

            @Override
            public void toXml(DynamicEntity instance, OutputStream out, boolean isRoot) {
                if (instance == null) {
                    return;
                }
                try {
                    //                    if (instance.entity != null) {
                    //                        instance.typeName = instance.entity.getClass().getSimpleName();
                    //                    }
                    if (isRoot) {
                        out.write(bs[0]);
                    }
                    if (instance.typeName != null && instance.entity != null) {
                        out.write(bs[1]);
                        out.write(instance.typeName.getBytes(ConstField.UTF8));
                        out.write(bs[2]);
                        out.write(bs[3]);
                        Serializer localSerializer = POJOSerializerProvider.getSerializer(instance.entity.getClass());
                        localSerializer.toXml(instance.entity, out, false);
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
            public void toJson(DynamicEntity instance, OutputStream out, boolean isRoot) {
                try {
                    out.write(JSON.toJSONBytes(instance));
                } catch (IOException localIOException) {
                    throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, localIOException);
                }
            }
        };
    }

    /**
     * note:PojoSerializer不支持动态类型，要让SerializerProvider支持要写很多恶心的代码，还是直接写java代码了
     */
    static Serializer<ObjectArrayResp> getObjectArrayRespSerializer() {
        return new Serializer<ObjectArrayResp>() {
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
                    out.write(JSON.toJSONBytes(instance));
                } catch (IOException localIOException) {
                    throw new ReturnCodeException(ApiReturnCode.UNKNOWN_ERROR, localIOException);
                }
            }
        };
    }

    void toXml(T instance, OutputStream out, boolean isRoot);

    void toJson(T instance, OutputStream out, boolean isRoot);
    //    void toProtobuf(GeneratedMessage instance, OutputStream out);

}
