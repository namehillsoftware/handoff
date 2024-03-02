package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

public class ProxyingPromise<Resolution> extends Promise<Resolution> implements CancellationResponse {

	private final CancellationProxy cancellationProxy = new CancellationProxy();

	protected ProxyingPromise() {
		awaitCancellation(this);
	}

	public void proxy(Promise<Resolution> promise) {
		try {
			promise.then(new InternalResolutionProxy(), new InternalRejectionProxy());
			cancellationProxy.doCancel(promise);
		} catch (Throwable throwable) {
			reject(throwable);
		}
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
