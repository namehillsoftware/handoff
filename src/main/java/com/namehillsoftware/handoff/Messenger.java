package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.PromiseLike;

public interface Messenger<Resolution> {
	void sendResolution(Resolution resolution);
	void sendRejection(Throwable error);
	PromiseLike<Void> promisedCancellation();
}
