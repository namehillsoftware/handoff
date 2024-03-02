package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

abstract class CancellableBroadcaster<Resolution> implements Cancellable {
	private final AtomicReference<CancellationResponse> reaction = new AtomicReference<>();
    private final AtomicBoolean isCancellationClosed = new AtomicBoolean();

    protected final void reject(Throwable error) {
        closeCancellationAndResolve(null, error);
    }

    protected final void resolve(Resolution resolution) {
        closeCancellationAndResolve(resolution, null);
    }

    private void closeCancellationAndResolve(Resolution resolution, Throwable rejection) {
        isCancellationClosed.set(true);
        this.reaction.set(null);
        resolve(resolution, rejection);
    }

    protected abstract void resolve(Resolution resolution, Throwable rejection);

    public final void cancel() {
        if (isCancellationClosed.getAndSet(true)) return;

        final CancellationResponse reactionSnapshot = reaction.getAndSet(null);
        if (reactionSnapshot != null)
            reactionSnapshot.cancellationRequested();
    }

    protected final void awaitCancellation(CancellationResponse reaction) {
        if (!isCancellationClosed.get())
            this.reaction.set(reaction);
    }
}
