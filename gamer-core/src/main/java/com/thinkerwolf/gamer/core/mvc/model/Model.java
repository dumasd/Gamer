package com.thinkerwolf.gamer.core.mvc.model;

/**
 * 请求后的数据模型，与View绑定
 *
 * @author wukai
 */
public interface Model<T> {

    T getData();

    String name();

    byte[] getBytes();

    /**
     * 数据是否是压缩过的
     *
     * @return
     */
    default boolean compress() {
        return encoding() != null && encoding().length() > 0;
    }

    /**
     * 压缩算法
     *
     * @return
     */
    default String encoding() {
        return null;
    }
}
