package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationToken;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CancellationProxy extends CancellationToken {
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
}
