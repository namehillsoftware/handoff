package com.namehillsoftware.handoff.cancellation;


import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationToken implements CancellationSignal, CancellationResponse {

	private final AtomicBoolean isCancelled = new AtomicBoolean();

	public final boolean isCancelled() {
		return isCancelled.get();
	}

	@Override
	public void cancellationRequested() {
		isCancelled.lazySet(true);
	}
}
