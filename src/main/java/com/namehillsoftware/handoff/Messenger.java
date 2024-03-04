package com.namehillsoftware.handoff;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.Promise;

public interface Messenger<Resolution> extends CancellationSignal {
	void sendResolution(Resolution resolution);
	void sendRejection(Throwable error);

	Promise<Void> promisedCancellation();
}
