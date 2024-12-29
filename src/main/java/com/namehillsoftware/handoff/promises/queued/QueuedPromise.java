package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

import java.util.concurrent.Executor;

public class QueuedPromise<Resolution> extends RunnablePromise<Resolution> {

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		super(task);
		executor.execute(this);
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		super(task);
		executor.execute(this);
	}
}
