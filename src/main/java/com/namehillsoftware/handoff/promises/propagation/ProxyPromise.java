package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

public class ProxyPromise<Resolution> extends Promise<Resolution> {

	private final CancellationProxy cancellationProxy = new CancellationProxy();

	public ProxyPromise(ProvideProxyablePromise<Resolution> provideProxyablePromise) {
		this();
		proxy(provideProxyablePromise.provide(cancellationProxy));
	}

	protected ProxyPromise() {
		awaitCancellation(cancellationProxy);
	}

	protected final void proxy(Promise<Resolution> promise) {
		try {
			cancellationProxy.doCancel(promise);
			promise.then(new InternalResolutionProxy(), new InternalRejectionProxy());
		} catch (Throwable throwable) {
			reject(throwable);
		}
	}

	protected CancellationProxy getCancellationProxy() {
		return cancellationProxy;
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
