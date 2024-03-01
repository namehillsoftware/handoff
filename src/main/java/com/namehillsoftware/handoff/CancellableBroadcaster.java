package com.namehillsoftware.handoff;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

abstract class CancellableBroadcaster<Resolution> {
    private final AtomicBoolean isCancellationClosed = new AtomicBoolean();

    protected final void reject(Throwable error) {
        closeCancellationAndResolve(null, error);
    }

    protected final void resolve(Resolution resolution) {
        closeCancellationAndResolve(resolution, null);
    }

    private void closeCancellationAndResolve(Resolution resolution, Throwable rejection) {
        isCancellationClosed.set(true);
        resolve(resolution, rejection);
    }

    protected abstract void resolve(Resolution resolution, Throwable rejection);

    public final void cancel() {
        if (!isCancellationClosed.getAndSet(true))
        	respondToCancellation();
    }

    protected void respondToCancellation() {}
}
