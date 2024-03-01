package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class CancellationProxy extends CancellationToken {
	private final Queue<Promise<?>> cancellablePromises = new ConcurrentLinkedQueue<>();

	public CancellationProxy doCancel(Promise<?> promise) {
		cancellablePromises.offer(promise);

		if (isCancelled()) act();

		return this;
	}

	@Override
	public void act() {
		Promise<?> cancellingPromise;
		while ((cancellingPromise = cancellablePromises.poll()) != null)
			cancellingPromise.cancel();
		super.act();
	}
}
