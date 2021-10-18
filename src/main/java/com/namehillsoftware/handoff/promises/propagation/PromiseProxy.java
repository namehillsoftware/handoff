package com.namehillsoftware.handoff.promises.propagation;


import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.Promise;

public class PromiseProxy<Resolution> {

	private final ResolutionProxy<Resolution> resolutionProxy;
	private final RejectionProxy rejectionProxy;
	private final CancellationProxy cancellationProxy = new CancellationProxy();

	public PromiseProxy(Messenger<Resolution> messenger) {
		this.resolutionProxy = new ResolutionProxy<>(messenger);
		this.rejectionProxy = new RejectionProxy(messenger);
		messenger.cancellationRequested(cancellationProxy);
	}

	public void proxy(Promise<Resolution> promise) {
		promise.then(resolutionProxy, rejectionProxy);

		cancellationProxy.doCancel(promise);
	}
}
