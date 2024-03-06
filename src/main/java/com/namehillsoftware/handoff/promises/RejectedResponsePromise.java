package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

final class RejectedResponsePromise<Resolution, Response> extends ImmediatelyRespondingMessenger<Resolution, Response> {
	private final ImmediateResponse<Throwable, Response> onFulfilled;

	RejectedResponsePromise(com.namehillsoftware.handoff.promises.response.ImmediateResponse<Throwable, Response> onRejected) {
		this.onFulfilled = onRejected;
	}

	@Override
	protected void respond(Resolution resolution) {}

	@Override
	protected void respond(Throwable throwable) throws Throwable {
		resolve(onFulfilled.respond(throwable));
	}
}
