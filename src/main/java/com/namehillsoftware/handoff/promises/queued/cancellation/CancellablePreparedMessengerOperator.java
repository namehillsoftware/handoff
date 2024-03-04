package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.Messenger;
import com.namehillsoftware.handoff.promises.MessengerOperator;

public final class CancellablePreparedMessengerOperator<Result> implements MessengerOperator<Result> {
	private final CancellableMessageWriter<Result> writer;

	public CancellablePreparedMessengerOperator(CancellableMessageWriter<Result> writer) {
		this.writer = writer;
	}

	@Override
	public void send(Messenger<Result> messenger) {
		try {
			messenger.sendResolution(writer.prepareMessage(messenger));
		} catch (Throwable throwable) {
			messenger.sendRejection(throwable);
		}
	}
}
