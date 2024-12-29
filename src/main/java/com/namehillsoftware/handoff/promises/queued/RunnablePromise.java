package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

public class RunnablePromise<Resolution> extends Promise<Resolution> implements Runnable {
	private final Runnable runnable;

	public RunnablePromise(CancellableMessageWriter<Resolution> task) {
		this.runnable = new QueuedCancellableMessageReader(task);
	}

	public RunnablePromise(MessageWriter<Resolution> task) {
		this.runnable = new QueuedMessageReader(task);
	}

	@Override
	public final void run() {
		this.runnable.run();
	}

	private final class QueuedMessageReader implements Runnable {
		private final MessageWriter<Resolution> task;

		public QueuedMessageReader(MessageWriter<Resolution> task) {
			this.task = task;
		}

		@Override
		public void run() {
			try {
				resolve(this.task.prepareMessage());
			} catch (Throwable e) {
				reject(e);
			}
		}
	}

	private final class QueuedCancellableMessageReader implements Runnable {
		private final CancellationProxy cancellationProxy = new CancellationProxy();
		private final CancellableMessageWriter<Resolution> task;

		private QueuedCancellableMessageReader(CancellableMessageWriter<Resolution> task) {
			RunnablePromise.this.awaitCancellation(cancellationProxy);
			this.task = task;
		}

		@Override
		public void run() {
			try {
				resolve(this.task.prepareMessage(cancellationProxy));
			} catch (Throwable e) {
				reject(e);
			}
		}
	}
}
