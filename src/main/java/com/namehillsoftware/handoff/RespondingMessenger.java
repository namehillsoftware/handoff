package com.namehillsoftware.handoff;


import org.jspecify.annotations.NonNull;

public interface RespondingMessenger<Input> {
	void respond(@NonNull Message<Input> message);
}
