package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;

public interface ImmediateCancellableResponse<Resolution, Response> {
	Response respond(Resolution resolution, CancellationSignal cancellationSignal) throws Throwable;
}
