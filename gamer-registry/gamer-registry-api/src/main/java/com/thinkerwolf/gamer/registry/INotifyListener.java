package com.thinkerwolf.gamer.registry;

import java.util.EventListener;

/**
 * 注册中心监听器
 *
 * @author wukai
 */
public interface INotifyListener extends EventListener {

    /**
     * 节点数据改变，删除或者修改
     *
     * @param event
     * @throws Exception
     */
    void notifyDataChange(DataEvent event) throws Exception;

    /**
     * 子节点改变
     *
     * @param event
     * @throws Exception
     */
    void notifyChildChange(ChildEvent event) throws Exception;
}
