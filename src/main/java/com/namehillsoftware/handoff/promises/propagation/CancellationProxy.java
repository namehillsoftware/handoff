package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.cancellation.PromisedCancellationToken;
import com.namehillsoftware.handoff.promises.response.ImmediateAction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class CancellationProxy extends PromisedCancellationToken implements ImmediateAction, CancellationResponse {
	private final Queue<Cancellable> cancellablePromises = new ConcurrentLinkedQueue<>();

	public void doCancel(Cancellable cancellable) {
		cancellablePromises.offer(cancellable);

		if (isCancelled()) cancellationRequested();
	}

	@Override
	public void cancellationRequested() {
		super.cancellationRequested();
		Cancellable cancellable;
		while ((cancellable = cancellablePromises.poll()) != null)
			cancellable.cancel();
	}

	@Override
	public void act() {
		cancellationRequested();
	}
}
