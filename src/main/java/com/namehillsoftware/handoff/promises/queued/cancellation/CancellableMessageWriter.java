package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;

public interface CancellableMessageWriter<Resolution> {
	Resolution prepareMessage(CancellationSignal cancellationSignal) throws Throwable;
}
