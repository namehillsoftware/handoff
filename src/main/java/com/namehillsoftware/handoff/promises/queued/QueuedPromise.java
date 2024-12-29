package com.namehillsoftware.handoff.promises.queued;

import com.namehillsoftware.handoff.promises.Promise;
import com.namehillsoftware.handoff.promises.propagation.CancellationProxy;
import com.namehillsoftware.handoff.promises.queued.cancellation.CancellableMessageWriter;

import java.util.concurrent.Executor;

public class QueuedPromise<Resolution> extends Promise<Resolution> {

	public QueuedPromise(CancellableMessageWriter<Resolution> task, Executor executor) {
		executor.execute(new QueuedCancellableMessageReader(task));
	}

	public QueuedPromise(MessageWriter<Resolution> task, Executor executor) {
		executor.execute(new QueuedMessageReader(task));
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
			QueuedPromise.this.awaitCancellation(cancellationProxy);
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
