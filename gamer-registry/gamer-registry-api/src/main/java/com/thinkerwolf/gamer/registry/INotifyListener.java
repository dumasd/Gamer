package com.thinkerwolf.gamer.registry;

import java.util.EventListener;

/**
 * Registry data listener. <strong>Never do long-running operations</strong>.
 *
 * @author wukai
 * @see com.thinkerwolf.gamer.registry.DataEvent
 * @see com.thinkerwolf.gamer.registry.ChildEvent
 */
public interface INotifyListener extends EventListener {

    /**
     * Notify node data change.
     *
     * @param event
     * @throws Exception
     */
    void notifyDataChange(DataEvent event) throws Exception;

    /**
     * Notify node children change.
     *
     * @param event
     * @throws Exception
     */
    void notifyChildChange(ChildEvent event) throws Exception;
}
