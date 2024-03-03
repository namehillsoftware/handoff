package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.response.ImmediateCancellableResponse;

class PromiseImmediateCancellableResponse<Resolution, Response> extends ImmediatelyRespondingCancellableMessenger<Resolution, Response> {

	private final ImmediateCancellableResponse<Resolution, Response> onFulfilled;
	private final ImmediateCancellableResponse<Throwable, Response> onRejected;

	PromiseImmediateCancellableResponse(ImmediateCancellableResponse<Resolution, Response> onFulfilled) {
		this(onFulfilled, null);
	}

	PromiseImmediateCancellableResponse(
			ImmediateCancellableResponse<Resolution, Response> onFulfilled,
			ImmediateCancellableResponse<Throwable, Response> onRejected) {
		this.onFulfilled = onFulfilled;
		this.onRejected = onRejected;
	}

	@Override
	protected void respond(Resolution resolution, CancellationSignal cancellationSignal) throws Throwable {
		resolve(onFulfilled.respond(resolution, cancellationSignal));
	}

	@Override
	protected void respond(Throwable rejection, CancellationSignal cancellationSignal) throws Throwable {
		if (onRejected != null)
			resolve(onRejected.respond(rejection, cancellationSignal));
		else
			reject(rejection);
	}
}
