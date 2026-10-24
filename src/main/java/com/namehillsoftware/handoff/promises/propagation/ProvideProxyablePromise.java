package com.namehillsoftware.handoff.promises.propagation;

import com.namehillsoftware.handoff.promises.Promise;
import org.jspecify.annotations.NonNull;

public interface ProvideProxyablePromise<Resolution> {
	@NonNull Promise<Resolution> provide(@NonNull CancellationProxy cancellationProxy);
}
