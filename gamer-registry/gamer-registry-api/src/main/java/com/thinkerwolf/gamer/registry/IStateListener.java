package com.thinkerwolf.gamer.registry;

import java.util.EventListener;

/**
 * Registry 状态改变
 *
 * @author wukai
 */
public interface IStateListener extends EventListener {

    void notifyStateChange(RegistryState state);

    void notifyNewSession();

    void notifyEstablishmentError(Throwable e);
}
