package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Message;
import com.namehillsoftware.handoff.RespondingMessenger;
import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.cancellation.CancellationToken;
import org.jspecify.annotations.NonNull;

abstract class ImmediatelyRespondingCancellableMessenger<Resolution, Response>
extends
	Promise<Response>
implements
	RespondingMessenger<Resolution> {

	private final CancellationToken cancellationToken = new CancellationToken();

	ImmediatelyRespondingCancellableMessenger() {
		awaitCancellation(cancellationToken);
	}

	@Override
	public final void respond(@NonNull Message<Resolution> message) {
		try {
			if (message.rejection == null)
				respond(message.resolution, cancellationToken);
			else
				respond(message.rejection, cancellationToken);
		} catch (Throwable error) {
			reject(error);
		}
	}

	protected abstract void respond(Resolution resolution, CancellationSignal cancellationSignal) throws Throwable;

	protected abstract void respond(Throwable reason, CancellationSignal cancellationSignal) throws Throwable;
}
