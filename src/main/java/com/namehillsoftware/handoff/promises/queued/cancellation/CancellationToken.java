package com.namehillsoftware.handoff.promises.queued.cancellation;


import com.namehillsoftware.handoff.promises.response.ImmediateAction;

import java.util.concurrent.atomic.AtomicBoolean;

public class CancellationToken implements ImmediateAction {

	private final AtomicBoolean isCancelled = new AtomicBoolean();

	public final boolean isCancelled() {
		return isCancelled.get();
	}

	@Override
	public void act() {
		isCancelled.lazySet(true);
	}
}
