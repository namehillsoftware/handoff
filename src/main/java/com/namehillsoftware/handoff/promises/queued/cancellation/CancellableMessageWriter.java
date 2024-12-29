package com.namehillsoftware.handoff.promises.queued.cancellation;

import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;

public interface CancellableMessageWriter<Resolution> {
	Resolution prepareMessage(CancellationProxy cancellationProxy) throws Throwable;
}
