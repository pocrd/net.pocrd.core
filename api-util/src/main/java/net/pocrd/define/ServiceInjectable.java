package net.pocrd.define;

/**
 * 处理接口间将A接口的返回结果作为B接口某个入参的执行器
 */
public interface ServiceInjectable {
    /**
     * 该参数的逻辑名称, 例如 product.productids
     */
    String getName();

    /**
     * 返回当前数据,
     * 如果数据类型是String会被直接使用
     * 其他类型会被转化为Json格式字符串来使用
     */
    Object getData(String jsonFromNotification);

    interface InjectionData {
        /**
         * 将同逻辑名的数据合并起来
         */
        void batchMerge(InjectionData injection);

        Object getData();

        String getName();
    }
}
