package com.namehillsoftware.handoff.promises;

final class RejectedResponsePromise<Resolution, Response> extends ImmediatelyRespondingMessenger<Resolution, Response> {
	private final com.namehillsoftware.handoff.promises.response.ImmediateResponse<Throwable, Response> onFulfilled;

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
