package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.cancellation.CancellationToken;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

public class DispatchablePromise<Resolution> extends Promise<Resolution> {

	private final CancellationToken cancellationToken = new CancellationToken();
	private final CancellableMessageWriter<Resolution> messageWriter;

	public DispatchablePromise(CancellableMessageWriter<Resolution> messageWriter) {
		awaitCancellation(cancellationToken);
		this.messageWriter = messageWriter;
	}

	public final void dispatchMessage() {
		try {
			resolve(messageWriter.prepareMessage(cancellationToken));
		} catch (Throwable e) {
			reject(e);
		}
	}
}
