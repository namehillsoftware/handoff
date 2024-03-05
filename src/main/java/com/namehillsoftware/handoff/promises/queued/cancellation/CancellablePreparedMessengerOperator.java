package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.cancellation.CancellableMessengerOperator;
import com.namehillsoftware.handoff.cancellation.CancellationToken;

public final class CancellablePreparedMessengerOperator<Result> extends CancellationToken implements CancellableMessengerOperator<Result> {
	private final CancellableMessageWriter<Result> writer;

	public CancellablePreparedMessengerOperator(CancellableMessageWriter<Result> writer) {
		this.writer = writer;
    }

	@Override
	public void send(Messenger<Result> messenger) {
		try {
			messenger.sendResolution(writer.prepareMessage(this));
		} catch (Throwable throwable) {
			messenger.sendRejection(throwable);
		}
	}
}
