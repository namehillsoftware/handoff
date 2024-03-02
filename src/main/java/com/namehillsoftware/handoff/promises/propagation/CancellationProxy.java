package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class CancellationProxy extends CancellationToken implements ImmediateAction {
	private final Queue<Promise<?>> cancellablePromises = new ConcurrentLinkedQueue<>();

	public void doCancel(Promise<?> promise) {
		cancellablePromises.offer(promise);

		if (isCancelled()) cancellationRequested();
	}

	@Override
	protected void cancellationRequested() {
		super.cancellationRequested();
		Promise<?> cancellingPromise;
		while ((cancellingPromise = cancellablePromises.poll()) != null)
			cancellingPromise.cancel();
	}

	@Override
	public void act() {
		cancel();
	}
}
