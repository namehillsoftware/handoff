package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

public abstract class DispatchablePromise<Resolution> extends Promise<Resolution> implements CancellableMessageWriter<Resolution> {

	private final CancellationToken cancellationToken = new CancellationToken();

	public DispatchablePromise() {
		awaitCancellation(cancellationToken);
	}

	public final void dispatchMessage() {
		try {
			resolve(prepareMessage(cancellationToken));
		} catch (Throwable e) {
			reject(e);
		}
	}
}
