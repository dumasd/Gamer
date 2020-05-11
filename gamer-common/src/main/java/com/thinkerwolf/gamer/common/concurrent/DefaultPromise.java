package com.thinkerwolf.gamer.common.concurrent;


import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * DefaultPromise
 *
 * @author wukai
 * @date 2020/5/11 13:14
 */
public class DefaultPromise<V> extends AbstractPromise<V> {

    private static final Object SUCCESS = new Object();
    private static final Object FAIL = new Object();
    private static final CauseHolder CANCEL = new CauseHolder(new CancellationException());
    private static final AtomicReferenceFieldUpdater<DefaultPromise, Object> resultUpdater =
            AtomicReferenceFieldUpdater.newUpdater(DefaultPromise.class, Object.class, "result");

    static {
        CANCEL.cause.setStackTrace(new StackTraceElement[]{new StackTraceElement(DefaultPromise.class.getName(), "cancel()", "", -1)});
    }

    private volatile Object result;
    private volatile int waiters;

    private Object attachment;

    public DefaultPromise() {
        super();
    }

    public DefaultPromise(Object attachment) {
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    @Override
    public boolean isSuccess() {
        return result != null && result != FAIL;
    }

    @Override
    public void setSuccess(V result) {
        setSuccess0(result);
    }

    @Override
    public Throwable cause() {
        Object obj = resultUpdater.get(this);
        if (obj instanceof CauseHolder) {
            return ((CauseHolder) obj).cause;
        }
        return null;
    }

    @Override
    public V getNow() {
        if (result == SUCCESS || result == FAIL || result instanceof CauseHolder) {
            return null;
        }
        return (V) result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (resultUpdater.compareAndSet(this, null, CANCEL)) {
            checkNotifyWaiters();
            notifyListeners();
            return true;
        }
        return false;
    }

    @Override
    public boolean isCancelled() {
        return resultUpdater.get(this) == CANCEL;
    }

    @Override
    public boolean isDone() {
        return resultUpdater.get(this) != null;
    }

    private void setSuccess0(V result) {
        Object o = result == null ? SUCCESS : result;
        if (resultUpdater.compareAndSet(this, null, o)) {
            checkNotifyWaiters();
            notifyListeners();
            return;
        }
        throw new IllegalStateException("Already finish");
    }

    @Override
    public void setFailure(Throwable cause) {
        setFailure0(cause);
    }

    private void setFailure0(Throwable cause) {
        Object o = cause == null ? FAIL : new CauseHolder(cause);
        if (resultUpdater.compareAndSet(this, null, o)) {
            checkNotifyWaiters();
            notifyListeners();
            return;
        }
        throw new IllegalStateException("Already finish");
    }

    private synchronized void checkNotifyWaiters() {
        if (waiters > 0) {
            notifyAll();
        }
    }

    @Override
    public Future<V> await() throws InterruptedException {
        await(-1, null);
        return this;
    }

    @Override
    public Future<V> await(long time, TimeUnit unit) throws InterruptedException {
        if (isDone()) {
            //notifyListeners();
            return this;
        }
        if (Thread.interrupted()) {
            throw new InterruptedException("Thread " + Thread.currentThread().getName() + " is interrupted");
        }
        final boolean timeout = time >= 0;
        if (timeout && unit == null) {
            unit = TimeUnit.MILLISECONDS;
        }
        long millis = 0;
        int nanos = 0;
        if (timeout) {
            time = unit.toNanos(time);
            millis = TimeUnit.NANOSECONDS.toMillis(time);
            nanos = (int) (time % millis);
        }
        synchronized (this) {
            while (!isDone()) {
                try {
                    incWaiters();
                    if (timeout) {
                        wait(millis, nanos);
                        break;
                    } else {
                        wait();
                    }
                } finally {
                    decWaiters();
                }
            }
        }
        return this;
    }

    private void incWaiters() {
        if (waiters >= Integer.MAX_VALUE) {
            throw new RuntimeException("too many waiters");
        }
        waiters++;
    }

    private void decWaiters() {
        waiters--;
    }

    private static class CauseHolder {
        Throwable cause;

        CauseHolder(Throwable cause) {
            this.cause = cause;
        }
    }
}
