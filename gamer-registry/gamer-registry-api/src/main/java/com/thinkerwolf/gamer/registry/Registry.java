package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;

import java.util.List;

/**
 * This is an interface used to abstract different registry operations. Like zookeeper, etcd etc.
 *
 * @author wukai
 * @since 2020/5/14 15:41
 */
public interface Registry extends AutoCloseable {
    /**
     * Obtain registry url
     *
     * @return url
     */
    URL url();

    /**
     * Register an url to registry.
     *
     * @param url The url to register.
     */
    void register(URL url);

    /**
     * Unregister an url to registry.
     *
     * @param url The url to unregister.
     */
    void unregister(URL url);

    /**
     * Close the registry.
     */
    @Override
    void close();

    /**
     * Lookup all urls below the given url.
     *
     * @param url The url to lookup.
     * @return All children urls.
     */
    List<URL> lookup(URL url);

    /**
     * Subscribe a listener to the give url.
     *
     * @param url
     * @param listener
     */
    void subscribe(URL url, INotifyListener listener);

    /**
     * Subscribe a listener from the give url.
     *
     * @param url
     * @param listener
     */
    void unsubscribe(URL url, INotifyListener listener);

    /**
     * Add a state listener.
     *
     * @param listener
     * @see RegistryState
     * @see IStateListener
     */
    void subscribeState(IStateListener listener);

    /**
     * Remove the state listener.
     *
     * @param listener
     * @see IStateListener
     */
    void unsubscribeState(IStateListener listener);
}
