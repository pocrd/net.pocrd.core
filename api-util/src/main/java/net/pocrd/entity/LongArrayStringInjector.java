package net.pocrd.entity;

import com.alibaba.fastjson.JSON;
import net.pocrd.define.ServiceInjectable;

/**
 * 用于处理半角逗号分隔的长整型数字字符串
 */
public abstract class LongArrayStringInjector implements ServiceInjectable {
    @Override
    public Object getData(String jsonFromNotification) {
        if (jsonFromNotification != null) {
            Data data = JSON.parseObject(jsonFromNotification, Data.class);
            return data.getData();
        }
        return null;
    }

    public static class Data implements InjectionData {
        private StringBuilder sb;
        private String        name;

        public Data(String name) {
            this.name = name;
        }

        @Override
        public void batchMerge(InjectionData injection) {
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(injection.getData());
            } else {
                sb.append(",").append(injection.getData());
            }
        }

        public void merge(long n) {
            if (sb == null) {
                sb = new StringBuilder();
                sb.append(n);
            } else {
                sb.append(",").append(n);
            }
        }

        @Override
        public String getData() {
            return sb.toString();
        }

        @Override
        public String getName() {
            return name;
        }

    }
}
