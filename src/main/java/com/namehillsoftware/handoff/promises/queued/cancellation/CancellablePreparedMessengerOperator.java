package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;

public final class CancellablePreparedMessengerOperator<Result> implements MessengerOperator<Result> {
	private final CancellableMessageWriter<Result> writer;

	public CancellablePreparedMessengerOperator(CancellableMessageWriter<Result> writer) {
		this.writer = writer;
    }

	@Override
	public void send(Messenger<Result> messenger) {
		try {
			final CancellationProxy cancellationProxy = new CancellationProxy();
			messenger.awaitCancellation(cancellationProxy);
			messenger.sendResolution(writer.prepareMessage(cancellationProxy));
		} catch (Throwable throwable) {
			messenger.sendRejection(throwable);
		}
	}
}
