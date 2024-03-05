package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.errors.RejectionDropper;
import com.namehillsoftware.handoff.promises.Promise;

import java.util.concurrent.CancellationException;

public class CancellationProxy extends CancellationToken {
	private final PromisedCancellation promisedCancellation = new PromisedCancellation();

	public CancellationProxy() {
		// Drop rejections so they don't by default go to the unhandled exception handler
		promisedCancellation.excuse(RejectionDropper.Instance.get());
	}

	public void doCancel(Cancellable cancellable) {
		promisedCancellation.then(new ImmediateCancellationResponse(cancellable));
	}

	@Override
	public void cancellationRequested() {
		super.cancellationRequested();
		promisedCancellation.resolveCancelled();
	}

	public final Promise<Void> promisedCancellation() {
		return promisedCancellation;
	}

	private static final class PromisedCancellation extends Promise<Void> implements CancellationResponse {

		public PromisedCancellation() {
			awaitCancellation(this);
		}

		@Override
		public void cancellationRequested() {
			reject(new PromisedCancellationCancelled());
		}

		public void resolveCancelled() {
			resolve(null);
		}
	}

	private static final class PromisedCancellationCancelled extends CancellationException {
		public PromisedCancellationCancelled() {
			super("No longer propagating cancellations.");
		}
	}
}
