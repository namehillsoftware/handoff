package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.response.ImmediateCancellableResponse;

final class RejectedCancellableResponsePromise<Resolution, Response>
	extends ImmediatelyRespondingCancellableMessenger<Resolution, Response> {
	private final ImmediateCancellableResponse<Throwable, Response> onFulfilled;

	RejectedCancellableResponsePromise(ImmediateCancellableResponse<Throwable, Response> onRejected) {
		this.onFulfilled = onRejected;
	}

	@Override
	protected void respond(Resolution resolution, CancellationSignal cancellationSignal) {}

	@Override
	protected void respond(Throwable throwable, CancellationSignal cancellationSignal) throws Throwable {
		resolve(onFulfilled.respond(throwable, cancellationSignal));
	}
}
