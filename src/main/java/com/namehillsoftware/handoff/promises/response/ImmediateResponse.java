package com.namehillsoftware.handoff.promises.response;

import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface ImmediateResponse<Resolution, Response> {
	Response respond(Resolution resolution) throws Throwable;
}
