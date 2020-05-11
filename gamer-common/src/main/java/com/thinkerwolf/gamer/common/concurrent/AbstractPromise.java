package com.thinkerwolf.gamer.common.concurrent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractPromise<V> extends AbstractFuture<V> implements Promise<V> {
    private List<FutureListener> listeners = new CopyOnWriteArrayList<>();

    protected void notifyListener(final FutureListener l) {
        if (isDone()) {
            try {
                l.operationComplete(this);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected void notifyListeners() {
        for (FutureListener<Future<V>> l : listeners) {
            if (l != null) {
                notifyListener(l);
            }
        }
    }

    @Override
    public Future<V> addListener(FutureListener listener) {
        listeners.add(listener);
        if (isDone()) {
            notifyListener(listener);
        }
        return this;
    }

    @Override
    public Future<V> addListeners(FutureListener... ls) {
        if (ls.length == 0) {
            return this;
        }
        for (FutureListener l : ls) {
            addListener(l);
        }
        return this;
    }
}
