package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

public class ProxyPromise<Resolution> extends Promise<Resolution> implements CancellationResponse {

	private final CancellationProxy cancellationProxy = new CancellationProxy();

	protected ProxyPromise() {
		awaitCancellation(this);
	}

	public final void proxy(Promise<Resolution> promise) {
		try {
			promise.then(new InternalResolutionProxy(), new InternalRejectionProxy());
			cancellationProxy.doCancel(promise);
		} catch (Throwable throwable) {
			reject(throwable);
		}
	}

	public void doCancel(Cancellable cancellable) {
		cancellationProxy.doCancel(cancellable);
	}

	@Override
	public void cancellationRequested() {
		cancellationProxy.cancellationRequested();
	}

	private final class InternalResolutionProxy implements ImmediateResponse<Resolution, Void> {

		@Override
		public Void respond(Resolution resolution) {
			resolve(resolution);
			return null;
		}
	}

	private final class InternalRejectionProxy implements ImmediateResponse<Throwable, Void> {

		@Override
		public Void respond(Throwable throwable) {
			reject(throwable);
			return null;
		}
	}
}
