package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.promises.Promise;

public interface ProvideProxyablePromise<Resolution> {
	Promise<Resolution> provide(CancellationProxy cancellationProxy);
}
