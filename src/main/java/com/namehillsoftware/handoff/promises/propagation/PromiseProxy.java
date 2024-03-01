package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.Promise;

public final class PromiseProxy<Resolution> {

	private final CancellationProxy cancellationProxy = new CancellationProxy();
	private final ResolutionProxy<Resolution> resolutionProxy;
	private final RejectionProxy rejectionProxy;

	public PromiseProxy(Messenger<Resolution> messenger) {
		resolutionProxy = new ResolutionProxy<>(messenger);
		rejectionProxy = new RejectionProxy(messenger);
		messenger.promisedCancellation().must(cancellationProxy);
	}

	public void proxy(Promise<Resolution> promise) throws Throwable {
		promise.then(resolutionProxy, rejectionProxy);

		cancellationProxy.doCancel(promise);
	}
}
