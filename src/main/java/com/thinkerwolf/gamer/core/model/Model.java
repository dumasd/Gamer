package com.thinkerwolf.gamer.core.model;

/**
 * 请求后的数据模型，与View绑定
 *
 * @author wukai
 */
public interface Model<T> {

    T getData();

    String name();

    byte[] getBytes();
}
