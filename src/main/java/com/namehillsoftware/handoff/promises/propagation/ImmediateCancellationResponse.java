package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.cancellation.Cancellable;
import com.namehillsoftware.handoff.promises.response.ImmediateResponse;

public final class ImmediateCancellationResponse implements ImmediateResponse<Void, Void> {
	private final Cancellable cancellable;

	public ImmediateCancellationResponse(Cancellable cancellable) {
		this.cancellable = cancellable;
	}

	@Override
	public Void respond(Void unused) {
		cancellable.cancel();
		return null;
	}
}
