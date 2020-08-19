package com.thinkerwolf.gamer.registry;

import java.util.EventListener;

/**
 * Registry state change listener. <strong>Never do long-running operations</strong>.
 *
 * @author wukai
 */
public interface IStateListener extends EventListener {
    /**
     * Notify when state change.
     *
     * @param state The new state
     * @see com.thinkerwolf.gamer.registry.RegistryState
     */
    void notifyStateChange(RegistryState state);

    /**
     * Notify new session created.
     */
    void notifyNewSession();

    /**
     * Notify connection error.
     *
     * @param e exception
     */
    void notifyEstablishmentError(Throwable e);
}
