package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Message;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.cancellation.CancellationToken;

abstract class ImmediatelyRespondingMessenger<Resolution, Response>
extends
	Promise<Response>
implements
	RespondingMessenger<Resolution> {

	private final CancellationToken cancellationToken = new CancellationToken();

	@Override
	public final void respond(Message<Resolution> message) {
		try {
			if (message.rejection == null)
				respond(message.resolution, cancellationToken);
			else
				respond(message.rejection, cancellationToken);
		} catch (Throwable error) {
			reject(error);
		}
	}

	@Override
	protected void cancellationRequested() {
		cancellationToken.cancel();
	}

	protected abstract void respond(Resolution resolution, CancellationSignal cancellationSignal) throws Throwable;

	protected abstract void respond(Throwable reason, CancellationSignal cancellationSignal) throws Throwable;
}
