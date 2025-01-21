package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.cancellation.CancellationSignal;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

import java.util.concurrent.Executor;

public class QueuedPromise<Resolution> extends ExecutablePromise<Resolution> {
	private final CancellableMessageWriter<Resolution> task;

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		super();
		this.task = task;
		executor.execute(this);
	}

	@Override
	public Resolution prepareMessage(CancellationSignal cancellationSignal) throws Throwable {
		return task.prepareMessage(cancellationSignal);
	}
}
