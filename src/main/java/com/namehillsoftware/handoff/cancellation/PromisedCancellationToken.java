package com.namehillsoftware.handoff.cancellation;

import com.namehillsoftware.handoff.promises.Promise;

public class PromisedCancellationToken extends CancellationToken {

	private final PromisedCancellation promisedCancellation = new PromisedCancellation();

	@Override
	public void cancellationRequested() {
		super.cancellationRequested();
		promisedCancellation.cancel();
	}

	public Promise<Void> promisedCancellation() {
		return promisedCancellation;
	}

	private static class PromisedCancellation extends Promise<Void> implements CancellationResponse {

		public PromisedCancellation() {
			awaitCancellation(this);
		}

		@Override
		public void cancellationRequested() {
			resolve(null);
		}
	}
}
