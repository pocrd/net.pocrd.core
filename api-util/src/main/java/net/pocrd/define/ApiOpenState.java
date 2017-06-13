package net.pocrd.define;

public enum ApiOpenState {
    /**
     * 接口对外开放
     */
    OPEN,
    /**
     * 接口关闭
     */
    CLOSED,
    /**
     * 接口不推荐使用
     */
    DEPRECATED,
    /**
     * 接口只生成文档，不产生服务代理
     */
    DOCUMENT
}
