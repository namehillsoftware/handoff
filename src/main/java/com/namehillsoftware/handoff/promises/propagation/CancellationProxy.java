package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.errors.RejectionDropper;
import com.namehillsoftware.handoff.promises.Promise;

import java.util.concurrent.CancellationException;

public class CancellationProxy extends Promise<Void> implements CancellationResponse, CancellationSignal {
	private volatile boolean isCancelled;

	public CancellationProxy() {
		// Drop rejections so they don't by default go to the unhandled exception handler
		excuse(RejectionDropper.Instance.get());
		awaitCancellation(new InternalCancellationResponse());
	}

	public final void doCancel(Cancellable cancellable) {
		then(new ImmediateCancellationResponse(cancellable));
	}

	@Override
	public final void cancellationRequested() {
		isCancelled = true;
		resolve(null);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	private final class InternalCancellationResponse implements CancellationResponse {

		@Override
		public void cancellationRequested() {
			reject(new PromisedCancellationCancelled());
		}
	}

	private static final class PromisedCancellationCancelled extends CancellationException {
		public PromisedCancellationCancelled() {
			super("No longer propagating cancellations.");
		}
	}
}
