package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

public class ProxyingPromise<Resolution> extends Promise<Resolution> {

	private final CancellationProxy cancellationProxy = new CancellationProxy();

	public void proxy(Promise<Resolution> promise) {
		try {
			promise.then(new InternalResolutionProxy(), new InternalRejectionProxy());
			cancellationProxy.doCancel(promise);
		} catch (Throwable throwable) {
			reject(throwable);
		}
	}

	@Override
	protected void cancellationRequested() {
		cancellationProxy.cancel();
		super.cancellationRequested();
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
