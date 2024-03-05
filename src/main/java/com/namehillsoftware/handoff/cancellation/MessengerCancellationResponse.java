package com.namehillsoftware.handoff.cancellation;

import com.namehillsoftware.handoff.Messenger;

public interface MessengerCancellationResponse<Resolution> {
	void cancellationRequested(Messenger<Resolution> messenger);
}
