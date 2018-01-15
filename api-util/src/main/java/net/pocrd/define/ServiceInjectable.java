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
     * 将请求数据封装为 InjectionData
     */
    InjectionData parseDataFromHttpParam(String param);

    /**
     * 返回其对应的数据类型
     */
    Class<? extends InjectionData> getDataType();

    /**
     * 执行器的相关数据类型, 该类型的数据对象被序列化成json后
     * 用于在dubbo服务之间相互传递隐式参数(使用notification)
     * 请保证实现类有public的无参构造函数
     */
    interface InjectionData<T> {
        /**
         * 将同逻辑名的数据合并起来
         */
        void batchMerge(InjectionData injection);

        T getValue();

        void setValue(T value);

        String getName();

        void setName(String name);
    }
}
