package com.namehillsoftware.handoff.promises.response;

import com.namehillsoftware.handoff.promises.Promise;
import org.jspecify.annotations.NonNull;

public interface PromisedResponse<Resolution, Response> {
	@NonNull Promise<Response> promiseResponse(Resolution resolution) throws Throwable;
}
