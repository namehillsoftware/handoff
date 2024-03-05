package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.CancellationResponse;

public interface Messenger<Resolution> {
	void sendResolution(Resolution resolution);
	void sendRejection(Throwable error);

	void awaitCancellation(CancellationResponse cancellationResponse);
}
