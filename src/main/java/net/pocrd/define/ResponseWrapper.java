package net.pocrd.define;

import net.pocrd.responseEntity.*;

import java.util.Collection;
import java.util.Date;

/**
 * Created by rendong on 15/11/12.
 */
public interface ResponseWrapper {
    Object wrap(Object obj);

    public static final ResponseWrapper boolArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : BoolArrayResp.convert((boolean[])obj);
        }
    };

    public static final ResponseWrapper boolWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : BoolResp.convert((Boolean)obj);
        }
    };

    public static final ResponseWrapper doubleArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DoubleArrayResp.convert((double[])obj);
        }
    };

    public static final ResponseWrapper doubleWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DoubleResp.convert((Double)obj);
        }
    };

    public static final ResponseWrapper floatArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DoubleArrayResp.convert((float[])obj);
        }
    };

    public static final ResponseWrapper floatWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DoubleResp.convert((Float)obj);
        }
    };

    public static final ResponseWrapper longArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : LongArrayResp.convert((long[])obj);
        }
    };

    public static final ResponseWrapper longWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : LongResp.convert((Long)obj);
        }
    };

    public static final ResponseWrapper byteArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberArrayResp.convert((byte[])obj);
        }
    };

    public static final ResponseWrapper byteWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberResp.convert((Byte)obj);
        }
    };

    public static final ResponseWrapper charArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberArrayResp.convert((char[])obj);
        }
    };

    public static final ResponseWrapper charWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberResp.convert((Character)obj);
        }
    };

    public static final ResponseWrapper shotArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberArrayResp.convert((short[])obj);
        }
    };

    public static final ResponseWrapper shortWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberResp.convert((Short)obj);
        }
    };

    public static final ResponseWrapper intArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberArrayResp.convert((int[])obj);
        }
    };

    public static final ResponseWrapper intWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : NumberResp.convert((Integer)obj);
        }
    };

    public static final ResponseWrapper objectArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : ObjectArrayResp.convert((Object[])obj);
        }
    };

    public static final ResponseWrapper objectCollectionWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : ObjectArrayResp.convert((Collection)obj);
        }
    };

    public static final ResponseWrapper objectWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj;
        }
    };

    public static final ResponseWrapper stringArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : StringArrayResp.convert((String[])obj);
        }
    };

    public static final ResponseWrapper stringCollectionWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : StringArrayResp.convert((Collection<String>)obj);
        }
    };

    public static final ResponseWrapper stringWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : StringResp.convert((String)obj);
        }
    };

    public static final ResponseWrapper dateArrayWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DateArrayResp.convert((Date[])obj);
        }
    };

    public static final ResponseWrapper dateCollectionWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DateArrayResp.convert((Collection<Date>)obj);
        }
    };

    public static final ResponseWrapper dateWrapper = new ResponseWrapper() {
        @Override
        public Object wrap(Object obj) {
            return obj == null ? null : DateResp.convert((Date)obj);
        }
    };
}
