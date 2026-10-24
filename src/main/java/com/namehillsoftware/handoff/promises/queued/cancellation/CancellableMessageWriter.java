package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public interface CancellableMessageWriter<Resolution> {
	Resolution prepareMessage(@NonNull CancellationSignal cancellationSignal) throws Throwable;
}
