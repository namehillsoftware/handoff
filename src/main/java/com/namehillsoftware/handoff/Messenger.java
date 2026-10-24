package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.CancellationResponse;
import org.jspecify.annotations.NonNull;

public interface Messenger<Resolution> {
	void sendResolution(Resolution resolution);
	void sendRejection(@NonNull Throwable error);

	void awaitCancellation(@NonNull CancellationResponse cancellationResponse);
}
