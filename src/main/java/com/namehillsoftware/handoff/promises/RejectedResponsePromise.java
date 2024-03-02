package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;

final class RejectedResponsePromise<Resolution, Response> extends ImmediatelyRespondingMessenger<Resolution, Response> {
	private final com.namehillsoftware.handoff.promises.response.ImmediateResponse<Throwable, Response> onFulfilled;

	RejectedResponsePromise(com.namehillsoftware.handoff.promises.response.ImmediateResponse<Throwable, Response> onRejected) {
		this.onFulfilled = onRejected;
	}

	@Override
	protected void respond(Resolution resolution, CancellationSignal cancellationSignal) {}

	@Override
	protected void respond(Throwable throwable, CancellationSignal cancellationSignal) throws Throwable {
		resolve(onFulfilled.respond(throwable));
	}
}
