package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.CancellationToken;

public interface Messenger<Resolution> {
	void sendResolution(Resolution resolution);
	void sendRejection(Throwable error);
	CancellationToken cancellation();
}
