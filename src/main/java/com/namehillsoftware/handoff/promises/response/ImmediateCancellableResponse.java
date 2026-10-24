package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface ImmediateCancellableResponse<Resolution, Response> {
	Response respond(Resolution resolution, @NonNull CancellationSignal cancellationSignal) throws Throwable;
}
