package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.DeferredProxyMessenger;
import com.namehillsoftware.handoff.promises.MessengerOperator;
import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellablePreparedMessengerOperator;

import java.util.concurrent.Executor;

public class QueuedPromise<Resolution> extends Promise<Resolution> {
	public QueuedPromise(MessengerOperator<Resolution> task, Executor executor) {
		super(new Execution.QueuedMessengerResponse<>(task, executor));
	}

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		this(new CancellablePreparedMessengerOperator<>(task), executor);
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		this(new PreparedMessengerOperator<>(task), executor);
	}

	private static class Execution {
		static final class QueuedMessengerResponse<Resolution> extends DeferredProxyMessenger<Resolution> {

			private final Executor executor;

			QueuedMessengerResponse(MessengerOperator<Resolution> task, Executor executor) {
				super(task);
				this.executor = executor;
			}

			@Override
			protected void execute(Runnable runnable) {
				executor.execute(runnable);
			}
		}
	}
}
