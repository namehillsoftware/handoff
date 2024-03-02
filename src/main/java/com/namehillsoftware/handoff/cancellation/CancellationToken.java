package com.namehillsoftware.handoff.cancellation;


import com.namehillsoftware.handoff.promises.Promise;

import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationToken extends Promise<Void> implements CancellationSignal, Cancellable, CancellationResponse {

	private final AtomicBoolean isCancelled = new AtomicBoolean();

	public final boolean isCancelled() {
		return isCancelled.get();
	}

	@Override
	public void cancellationRequested() {
		isCancelled.lazySet(true);
		resolve(null);
	}
}
