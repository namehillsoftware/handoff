package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.promises.Promise;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface PromisedResponse<Resolution, Response> {
	@NonNull Promise<Response> promiseResponse(Resolution resolution) throws Throwable;
}
