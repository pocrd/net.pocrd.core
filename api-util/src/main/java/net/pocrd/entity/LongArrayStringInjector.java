package net.pocrd.entity;

import net.pocrd.define.ServiceInjectable;

/**
 * 用于处理半角逗号分隔的长整型数字字符串
 */
public abstract class LongArrayStringInjector implements ServiceInjectable {
    @Override
    public InjectionData parseDataFromHttpParam(String param) {
        Data data = new Data(getName());
        data.sb = new StringBuilder(param);
        return data;
    }

    @Override
    public Class<Data> getDataType() {
        return Data.class;
    }

    public static class Data implements InjectionData<String> {
        // 当sb不为空的时候,使用它存储的值
        private StringBuilder sb;
        // 从封装类型传递过来的名称
        private String        name;
        // 初始化的值数据存储在该字段中
        private String        value;

        private Data() {
        }

        public Data(String name) {
            this.name = name;
        }

        public Data(String name, String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public void batchMerge(InjectionData injection) {
            if (sb == null) {
                if (value == null) {
                    value = injection.getValue().toString();
                    return;
                } else {
                    sb = new StringBuilder(value);
                }
            }
            sb.append(",").append(injection.getValue());
        }

        public void merge(long n) {
            if (sb == null) {
                if (value == null) {
                    sb = new StringBuilder();
                    sb.append(n);
                    return;
                } else {
                    sb = new StringBuilder(value);
                }
            }
            sb.append(",").append(n);
        }

        @Override
        public String getValue() {
            return sb == null ? value : sb.toString();
        }

        @Override
        public void setValue(String value) {
            this.value = value;
            sb = null;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }
    }
}
