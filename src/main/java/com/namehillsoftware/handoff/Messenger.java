package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.promises.Promise;

public interface Messenger<Resolution> {
	void sendResolution(Resolution resolution);
	void sendRejection(Throwable error);
	Promise<Void> promisedCancellation();
}
