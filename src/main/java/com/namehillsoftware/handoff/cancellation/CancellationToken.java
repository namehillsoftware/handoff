package com.namehillsoftware.handoff.cancellation;


import com.namehillsoftware.handoff.promises.Promise;

import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationToken extends Promise<Void> implements CancellationSignal, Cancellable {

	public CancellationToken() {
		super((CancellationToken)null);
	}

	private final AtomicBoolean isCancelled = new AtomicBoolean();

	public final boolean isCancelled() {
		return isCancelled.get();
	}

	@Override
	protected void cancellationRequested() {
		isCancelled.lazySet(true);
		resolve(null);
	}
}
