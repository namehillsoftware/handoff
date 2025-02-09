package com.namehillsoftware.handoff.cancellation;


public class CancellationToken implements CancellationSignal, CancellationResponse {

	private volatile boolean isCancelled;

	@Override
	public final boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void cancellationRequested() {
		isCancelled = true;
	}
}
