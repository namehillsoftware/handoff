package com.namehillsoftware.handoff.promises;

import com.namehillsoftware.handoff.Messenger;
import org.jspecify.annotations.NonNull;

public interface MessengerOperator<Resolution> {
	void send(@NonNull Messenger<Resolution> messenger);
}
